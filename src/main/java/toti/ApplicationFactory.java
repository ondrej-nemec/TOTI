package toti;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.functions.Hash;
import ji.common.structures.MapDictionary;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.database.support.SqlQueryProfiler;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.application.Task;
import toti.profiler.Profiler;
import toti.register.Register;
import toti.security.AuthenticationCache;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.IdentityFactory;
import toti.templating.TemplateFactory;
import toti.url.Link;
import toti.url.LoadUrls;
import toti.url.UrlPart;

public class ApplicationFactory {
	
	/*
		"CSP:frame-ancestors 'none'" // nacteni stranky ve framu
		, "Content-Security-Policy-Report-Only"
			+ " script-src 'strict-dynamic' 'nonce-{nonce}' 'unsafe-inline' http: https:;"
			+ " object-src 'none';"
			+ " form-action 'self';"
			+ " report-uri '/entity/api/entity/reporting'"
		, "Access-Control-Allow-Origin: *"
	*/
	
	private String tempPath = null;
	private String resourcesPath = null;
	private Boolean deleteTempJavaFiles = null;
	private Boolean dirResponseAllowed = null;
	private String dirDefaultFile = null;
	private Boolean minimalize = null;
	private List<String> developIps = null;
	private Long tokenExpirationTime = null;
	private String tokenCustomSalt = null;
	private Boolean useProfiler = null;
	private String urlPattern = null;
	private String logsPath = null;
	
	private Boolean autoStart = null;

	private Headers responseHeaders = null;
	private LanguageSettings langSettings = null;
	//private Database database = null;
	private BiFunction<List<String>, Env, Database> createDatabase = null;
	private Translator translator = null;
	
	private Env appEnv;
	
	private final Env env;
	private BiFunction<String, String, Logger> loggerFactory;
	private final String hostname;
	private final String charset;
	
	public ApplicationFactory(String hostname, Env env, String charset, Function<String, Logger> loggerFactory) {
		this.env = env;
		this.loggerFactory = (hostName, loggerName)->loggerFactory.apply(hostname+ "_" + loggerName);
		this.hostname = hostname;
		this.charset = charset;
	}

	public Application create(List<Module> modules) throws Exception {
		Logger logger = loggerFactory.apply(hostname, "toti");
		
		List<String> migrations = new LinkedList<>();
		modules.forEach((config)->{
			if (config.getMigrationsPath() != null) {
				migrations.add(config.getMigrationsPath());
			}
		});
		Env env = appEnv = this.env.getModule("applications").getModule(hostname);
		Profiler profiler = initProfiler(env, logger);
		
		Database database = getDatabase(
			env.getModule("database"), migrations, profiler.used(), loggerFactory.apply(hostname, "database")
		);
		if (database == null) {
			logger.info("No database specified");
		}
		
		
		Register register = new Register();
		Link link = new Link(getUrlPattern(env), register);
		Router router = new Router(link);
		
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		Set<String> trans = new HashSet<>();
		trans.add("toti/translations");
		List<Task> tasks = new LinkedList<>();
		if (translator == null) {
			LanguageSettings langSettings = getLangSettings(env);
			langSettings.setProfiler(profiler.used());
			this.translator = Translator.create(langSettings, trans, loggerFactory.apply(hostname, "translator"));
		}
		MapDictionary<UrlPart, Object> mapping = MapDictionary.hashMap();
		for (Module module : modules) {
			TemplateFactory templateFactory = new TemplateFactory(
					getTempPath(env, hostname),
					module.getTemplatesPath(),
					module.getName(),
					module.getPath(),
					templateFactories, 
					getDeleteTempJavaFiles(env),
					getMinimalize(env),
					logger
			);
			templateFactory.setProfiler(profiler.used());
			templateFactories.put(module.getName(), templateFactory);
			if (module.getTranslationPath() != null) {
				trans.add(module.getTranslationPath());
				trans.add(module.getPath() + "/" + module.getTranslationPath());
			}
			tasks.addAll(
				module.initInstances(
					env, translator, register, link, database,
					loggerFactory.apply(hostname, module.getName())
				)
			);
			LoadUrls.loadUrlMap(mapping, module, router, register, link);
			module.addRoutes(router);
		};
		// file session save is disabled - maybe enable hibrid saving - user in memory, some user data on disk
		AuthenticationCache sessionCache = new AuthenticationCache(hostname, getTempPath(env, hostname), false, logger);
		ResponseFactory response = new ResponseFactory(
				getResponseHeaders(env),
				getResourcesPath(env),
				router,
				templateFactories,
				new TemplateFactory(
					getTempPath(env, hostname), "toti/web", "", "", templateFactories,
					getDeleteTempJavaFiles(env), getMinimalize(env),
					logger
				).setProfiler(profiler.used()),
				translator,
				new IdentityFactory(translator, translator.getLocale().getLang()),
				new Authenticator(
					getTokenExpirationTime(env), getTokenCustomSalt(env), 
					sessionCache,
					Hash.getSha256(),
					logger
				),
				new Authorizator(logger),
				charset,
				getDirResponseAllowed(env),
				getDirDefaultFile(env),
				getLogsPath(env),
				getDevelopIps(env),
				logger,
				profiler,
				register,
				mapping
		);
		return new Application(tasks, sessionCache, translator, database, link, register, migrations, response, getAutoStart(env));
	}

	private Profiler initProfiler(Env env, Logger logger) {
		Profiler profiler = new Profiler();
		profiler.setUse(getUseProfiler(env));
		if (profiler.isUse()) {
			logger.warn("Profiler is enabled");
		}
		return profiler;
	}
		
	/*************************/
	
	private Database getDatabase(Env env, List<String> migrations, SqlQueryProfiler profiler, Logger logger) {
		if (createDatabase != null) {
			return createDatabase.apply(migrations, env);
		}
		if (env != null && env.getString("type") != null) {
			return new Database(new DatabaseConfig(
				env.getString("type"),
				env.getString("url"),
				env.getBoolean("external") == null ? true : env.getBoolean("external"),
				env.getString("schema-name"),
				env.getString("login"),
				env.getString("password"),
				migrations,
				env.getInteger("pool-size")
			), profiler, logger);
		}
		return null;
	}
	
	private String getUrlPattern(Env env) {
		return getProperty(urlPattern, "url-pattern", "/[module]</[path]>/[controller]/[method]</[param]>", String.class, env);
	}
	
	private String getTempPath(Env env, String hostname) {
		return getProperty(tempPath, "temp", "temp", String.class, env) + "/" + hostname;
	}
	
	public String getTempPath() {
		return getTempPath(appEnv, hostname);
	}
	
	private String getResourcesPath(Env env) {
		return getProperty(resourcesPath, "resource-path", "www", String.class, env);
	}
	
	private Boolean getDeleteTempJavaFiles(Env env) {
		return getProperty(deleteTempJavaFiles, "delete-temp-java", true, Boolean.class, env);
	}
	
	private Boolean getDirResponseAllowed(Env env) {
		return getProperty(dirResponseAllowed, "dir-allowed", false, Boolean.class, env);
	}
	
	private String getDirDefaultFile(Env env) {
		return getProperty(dirDefaultFile, "dir-default-file", "index.html", String.class, env);
	}
	
	private Boolean getMinimalize(Env env) {
		return getProperty(minimalize, "minimalize-templates", true, Boolean.class, env);
	}
	
	private List<String> getDevelopIps(Env env) {
		if (developIps != null) {
			return developIps;
		}
		String key = "ip";
		if (env != null && env.getValue(key) != null) {
			return env.getList(key, "\\|");
		}
		return Arrays.asList("/127.0.0.1", "/0:0:0:0:0:0:0:1");
	}
	
	private Long getTokenExpirationTime(Env env) {
		return getProperty(tokenExpirationTime, "token-expired",  1000L * 60 * 10, Long.class, env);
	}
	
	private String getTokenCustomSalt(Env env) {
		return getProperty(tokenCustomSalt, "token-salt", "", String.class, env);
	}
	
	private Boolean getUseProfiler(Env env) {
		return getProperty(useProfiler, "use-profiler", false, Boolean.class, env);
	}
	
	private String getLogsPath(Env env) {
		return getProperty(logsPath, "logs-path", "logs",  String.class, env);
	}
	
	private LanguageSettings getLangSettings(Env conf) {
		if (langSettings != null) {
			return langSettings;
		}
		Env env = conf.getModule("lang");
		if (env.getString("locales") != null) {  
			List<Locale> locales = new LinkedList<>();
			for (String l : env.getString("locales").split(",")) {
				String locale = l.trim();
				Env langConf = env.getModule("locale").getModule(locale);
				Boolean ltr = langConf.getBoolean("ltr");
				String substitutions = langConf.getString("substitutions");
				locales.add(new Locale(
					locale,
					ltr == null ? true : ltr,
					substitutions == null ? Arrays.asList() : Arrays.asList(substitutions.split(",")) 
				));
			}
			return new LanguageSettings(env.getString("default"), locales);
		}
		return new LanguageSettings(java.util.Locale.getDefault().toString(), Arrays.asList());
	}
	
	private Headers getResponseHeaders(Env env) {
		if (responseHeaders != null) {
			return responseHeaders;
		}
		if (env.getValue("headers") != null) {
			Headers headers = new Headers();
			env.getList("headers", "\\|").forEach(h->{
				String[] hds = h.split(":", 2);
				if (hds.length == 1) {
					headers.addHeader(hds[0].trim(), "");
				} else {
					headers.addHeader(hds[0].trim(), hds[1].trim());
				}
			});
		}
		return new Headers() .addHeader("Access-Control-Allow-Origin", "*");
	}
	
	private boolean getAutoStart(Env env) {
		return getProperty(autoStart, "autostart", true, Boolean.class, env);
	}
	
	private <T> T getProperty(T value,String key, T defaultValue, Class<T> clazz, Env env) {
		if (value != null) {
			return value;
		}
		if (env != null && env.getValue(key) != null) {
			return env.getDictionaryValue(key).getValue(clazz);
		}
		return defaultValue;
	}
	
	/*************************/

	public ApplicationFactory setDirResponseAllowed(boolean dirResponseAllowed) {
		this.dirResponseAllowed = dirResponseAllowed;
		return this;
	}
	
	public ApplicationFactory setDirDefaultFile(String dirDefaultFile) {
		this.dirDefaultFile = dirDefaultFile;
		return this;
	}

	public ApplicationFactory setHeaders(Map<String, List<Object>> headers) {
		this.responseHeaders = new Headers(headers);
		return this;
	}

	public ApplicationFactory setHeaders(Headers headers) {
		this.responseHeaders = headers;
		return this;
	}
	
	public ApplicationFactory setLogsPath(String logsPath) {
		this.logsPath = logsPath;
		return this;
	}

	public ApplicationFactory setTempPath(String tempPath) {
		this.tempPath = tempPath;
		return this;
	}

	public ApplicationFactory setLanguageSettings(LanguageSettings settings) {
		this.langSettings = settings;
		return this;
	}
	
	public ApplicationFactory setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
		return this;
	}
	
	public ApplicationFactory setTokenCustomSalt(String tokenCustomSalt) {
		this.tokenCustomSalt = tokenCustomSalt;
		return this;
	}

	public ApplicationFactory setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
		return this;
	}

	public ApplicationFactory setDeleteTempJavaFiles(boolean deleteTempJavaFiles) {
		this.deleteTempJavaFiles = deleteTempJavaFiles;
		return this;
	}

	public ApplicationFactory setMinimalize(boolean minimalize) {
		this.minimalize = minimalize;
		return this;
	}

	public ApplicationFactory setDevelopIpAdresses(List<String> developIps) {
		this.developIps = developIps;
		return this;
	}
	
	public ApplicationFactory setUseProfiler(boolean useProfiler) {
		this.useProfiler = useProfiler;
		return this;
	}

	public ApplicationFactory setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		return this;
	}
	
	public ApplicationFactory setTranslator(Translator translator) {
		this.translator = translator;
		return this;
	}
	
	public ApplicationFactory setLoggerFactory(BiFunction<String, String, Logger> loggerFactory) {
		this.loggerFactory = loggerFactory;
		return this;
	}
	
	public ApplicationFactory setDatabase(BiFunction<List<String>, Env, Database> createDatabase) {
		this.createDatabase = createDatabase;
		return this;
	}
	
	public ApplicationFactory setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}
	
}
