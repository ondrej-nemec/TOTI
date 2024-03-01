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
import ji.common.structures.ObjectBuilder;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.database.support.SqlQueryProfiler;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.answers.Answer;
import toti.answers.ControllerAnswer;
import toti.answers.ExceptionAnswer;
import toti.answers.FileSystemAnswer;
import toti.answers.Headers;
import toti.answers.TotiAnswer;
import toti.answers.request.IdentityFactory;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.answers.router.UriPattern;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Param;
import toti.application.register.Register;
import toti.extensions.Extension;
import toti.extensions.Profiler;
import toti.extensions.TranslatedExtension;
import toti.extensions.OnSession;
import toti.extensions.OnToti;
import toti.templating.TemplateFactory;

public class ApplicationFactory {
	
	private String tempPath = null;
	private String resourcesPath = null;
	private Boolean deleteTempJavaFiles = null;
	private Boolean dirResponseAllowed = null;
	private String dirDefaultFile = null;
	private Boolean minimalize = null;
	private List<String> developIps = null;
//	private Long tokenExpirationTime = null;
//	private String tokenCustomSalt = null;
	private Boolean useProfiler = null;
	// private String urlPattern = null;
	private String logsPath = null;
	
	private Boolean autoStart = null;

	private Map<String, List<Object>>  responseHeaders = null;
	private LanguageSettings langSettings = null;
	//private Database database = null;
	private BiFunction<List<String>, Env, Database> createDatabase = null;
	private Translator translator = null;
	private UriPattern pattern = new UriPattern() {};
	
	private String[] aliases;
	private String hostname;
	
	//private Env appEnv;
	
	private final Env env;
	private BiFunction<String, String, Logger> loggerFactory;
	private final String appIdentifier;
	private final String charset;
	
	private final List<OnSession> sessions;
	private final List<OnToti> totiResponses;
	private final List<String> translationPaths;
	private Profiler profiler;
	
	public ApplicationFactory(String appIdentifier, Env env, String charset, Function<String, Logger> loggerFactory,
		String hostname, String... aliases) {
		this.env = env.getModule("applications").getModule(appIdentifier);
		this.loggerFactory = (hostName, loggerName)->loggerFactory.apply(appIdentifier+ "_" + loggerName);
		this.appIdentifier = appIdentifier;
		this.charset = charset;
		this.aliases = aliases;
		this.hostname = hostname;
		
		this.sessions = new LinkedList<>();
		this.totiResponses = new LinkedList<>();
		this.translationPaths = new LinkedList<>();
	}

	public Application create(List<Module> modules) throws Exception {
		Logger logger = loggerFactory.apply(appIdentifier, "toti");
		
		List<String> migrations = new LinkedList<>();
		modules.forEach((config)->{
			if (config.getMigrationsPath() != null) {
				migrations.add(config.getMigrationsPath());
			}
		});
		
	//	Env env = appEnv = this.env.getModule("applications").getModule(hostname);
		Profiler profiler = initProfiler(env, logger);
		
		
		Database database = getDatabase(
			env.getModule("database"), migrations, profiler, loggerFactory.apply(appIdentifier, "database")
		);
		if (database == null) {
			logger.info("No database specified");
		}
		
		ObjectBuilder<Module> actualModule = new ObjectBuilder<>();
		Param root = new Param(null);
		Register register = new Register(root, actualModule, pattern);
		Link link = new Link(/*getUrlPattern(env),*/ register, pattern);
		Router router = new Router(/*register*/);
		
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		Set<String> trans = new HashSet<>();
		trans.addAll(translationPaths);
		List<Task> tasks = new LinkedList<>();
		if (translator == null) {
			LanguageSettings langSettings = getLangSettings(env);
			langSettings.setProfiler(profiler);
			this.translator = Translator.create(langSettings, trans, loggerFactory.apply(appIdentifier, "translator"));
		}
		for (Module module : modules) {
			actualModule.set(module);
			TemplateFactory templateFactory = new TemplateFactory(
					getTempPath(env, appIdentifier),
					module.getTemplatesPath(),
					module.getName(),
					module.getPath(),
					templateFactories, 
					getDeleteTempJavaFiles(env),
					getMinimalize(env),
					logger
			);
			templateFactory.setProfiler(profiler);
			templateFactories.put(module.getName(), templateFactory);
			if (module.getTranslationPath() != null) {
				trans.add(module.getTranslationPath());
				trans.add(module.getPath() + "/" + module.getTranslationPath());
			}
			tasks.addAll(
				module.initInstances(
					env, translator, register, link, database,
					loggerFactory.apply(appIdentifier, module.getName())
				)
			);
			module.addRoutes(router, link);
		};
		actualModule.set(null);
		
		TemplateFactory totiTemplateFactory = new TemplateFactory(
			getTempPath(env, appIdentifier), "toti", "", "", templateFactories,
			getDeleteTempJavaFiles(env), getMinimalize(env),
			logger
		).setProfiler(profiler);
		
		IdentityFactory identityFactory = new IdentityFactory(
			translator, translator.getLocale().getLang(), sessions, register.getSessionUserProvider()
		);
		
		List<String> developIps = getDevelopIps(env);
		TotiAnswer totiAnwer = new TotiAnswer(
			developIps, totiTemplateFactory, translator, identityFactory, totiResponses
		);
		ExceptionAnswer exceptionAnswer = new ExceptionAnswer(
			register,
			developIps,
			totiTemplateFactory,
			getLogsPath(env),
			translator,
			logger
		);
		ControllerAnswer controllerAnswer = new ControllerAnswer(
			router, root, templateFactories, register.getSessionUserProvider(), identityFactory, link, translator, logger
		);
		FileSystemAnswer fileSystemAnswer = new FileSystemAnswer(
			getResourcesPath(env),
			getDirResponseAllowed(env),
			getDirDefaultFile(env),
			logger
		);
		Answer answer = new Answer(
			exceptionAnswer,
			controllerAnswer,
			fileSystemAnswer,
			totiAnwer,
			identityFactory,
			getResponseHeaders(env),
			charset
		);
		return new Application(
			tasks, translator, root, database, link, register, migrations,
			answer, getAutoStart(env), hostname, aliases
		);
	}

	private Profiler initProfiler(Env env, Logger logger) {
		if (getUseProfiler(env) && profiler != null) {
			logger.warn("Profiler is enabled");
			return profiler;
		}
		if (getUseProfiler(env) && profiler == null) {
			logger.warn("Profiler is enabled but no profiler set.");
		}
		return Profiler.empty();
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
	/*
	private String getUrlPattern(Env env) {
		return getProperty(urlPattern, "url-pattern", "/[module]</[path]>/[controller]/[method]</[param]>", String.class, env);
	}
	*/
	private String getTempPath(Env env, String hostname) {
		return getProperty(tempPath, "temp", "temp", String.class, env) + "/" + hostname;
	}
	
	public String getTempPath() {
		return getTempPath(env, appIdentifier);
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
	/*
	private Long getTokenExpirationTime(Env env) {
		return getProperty(tokenExpirationTime, "token-expired",  1000L * 60 * 10, Long.class, env);
	}
	
	private String getTokenCustomSalt(Env env) {
		return getProperty(tokenCustomSalt, "token-salt", "", String.class, env);
	}
	*/
	private Boolean getUseProfiler(Env env) {
		return getProperty(useProfiler, "use-profiler", false, Boolean.class, env);
	}
	
	private String getLogsPath(Env env) {
		return getProperty(logsPath, "logs-path", "logs" + "/" + appIdentifier,  String.class, env);
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
	
	private Map<String, List<Object>>  getResponseHeaders(Env env) {
		if (responseHeaders != null) {
			return responseHeaders;
		}
		Headers headers = new Headers();
		if (env.getValue("headers") != null) {
			env.getList("headers", "\\|").forEach(h->{
				String[] hds = h.split(":", 2);
				if (hds.length == 1) {
					headers.addHeader(hds[0].trim(), "");
				} else {
					headers.addHeader(hds[0].trim(), hds[1].trim());
				}
			});
		} else {
			/*
			"CSP:frame-ancestors 'none'" // nacteni stranky ve framu
			, "Content-Security-Policy-Report-Only"
				+ " script-src 'strict-dynamic' 'nonce-{nonce}' 'unsafe-inline' http: https:;"
				+ " object-src 'none';"
				+ " form-action 'self';"
				+ " report-uri '/entity/api/entity/reporting'"
			, "Access-Control-Allow-Origin: *"
		*/
		
			headers.addHeader("Access-Control-Allow-Origin", "*");
		}
		return headers.getHeaders();
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
	
	public ApplicationFactory setUrlPattern(UriPattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public ApplicationFactory addExtension(Extension extension) {
		if (extension instanceof OnSession) {
			sessions.add((OnSession)extension);
		}
		if (extension instanceof OnToti) {
			totiResponses.add((OnToti)extension);
		}
		if (extension instanceof Profiler) {
			this.profiler = (Profiler)extension;
		}
		if (extension instanceof TranslatedExtension) {
			translationPaths.add(TranslatedExtension.class.cast(extension).getTranslationPath());
		}
		return this;
	}

	public ApplicationFactory setDirResponseAllowed(boolean dirResponseAllowed) {
		this.dirResponseAllowed = dirResponseAllowed;
		return this;
	}
	
	public ApplicationFactory setDirDefaultFile(String dirDefaultFile) {
		this.dirDefaultFile = dirDefaultFile;
		return this;
	}

	public ApplicationFactory setHeaders(Map<String, List<Object>> headers) {
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
	/*
	public ApplicationFactory setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
		return this;
	}
	
	public ApplicationFactory setTokenCustomSalt(String tokenCustomSalt) {
		this.tokenCustomSalt = tokenCustomSalt;
		return this;
	}
*/
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
/*
	public ApplicationFactory setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		return this;
	}
	*/
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
