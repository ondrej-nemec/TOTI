package toti;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.functions.Hash;
import ji.common.structures.MapDictionary;
import ji.database.Database;
import ji.socketCommunication.Server;
import ji.socketCommunication.SslCredentials;
import ji.socketCommunication.http.RestApiServer;
import ji.translator.LanguageSettings;
import ji.translator.Translator;
import toti.application.Task;
import toti.logging.TotiLoggerFactory;
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

public class HttpServerFactory {
	
	// todo maybe regist here too OR only list - new header in creating response
	private Headers responseHeaders = new Headers()
			.addHeader("Access-Control-Allow-Origin", "*");
	/*
		"CSP:frame-ancestors 'none'" // nacteni stranky ve framu
		, "Content-Security-Policy-Report-Only"
			+ " script-src 'strict-dynamic' 'nonce-{nonce}' 'unsafe-inline' http: https:;"
			+ " object-src 'none';"
			+ " form-action 'self';"
			+ " report-uri '/entity/api/entity/reporting'"
		, "Access-Control-Allow-Origin: *"
	*/
	
	private Translator translator;
	
	private int port = 80;
	private int threadPool = 5;
	private long readTimeout = 60000;
	private Optional<SslCredentials> certs = Optional.empty();
	private String tempPath = "temp";
	private String charset = "UTF-8";
	private LanguageSettings settings = new LanguageSettings(Locale.getDefault().toString(), Arrays.asList());
	private String resourcesPath = "www";
	private boolean deleteTempJavaFiles = true;
	private boolean dirResponseAllowed = false;
	private String dirDefaultFile = "index.html";
	private boolean minimalize = true;
	private List<String> developIps = Arrays.asList("/127.0.0.1", "/0:0:0:0:0:0:0:1");
	private Integer maxUploadFileSize = null;
	private long tokenExpirationTime = 1000 * 60 * 10;
	private String tokenCustomSalt = "";
	private boolean useProfiler = false;
	private String urlPattern = "/[module]</[path]>/[controller]/[method]</[param]>";
	private Function<String, Logger> loggerFactory = TotiLoggerFactory.get();
	private String logsPath = "logs";
	
	public HttpServerFactory() {}
	
	public <T extends Module> HttpServer get(List<T> modules, Env env, Database database) throws Exception {
		// maybe more - separated - loggers??
		Logger logger = loggerFactory.apply("toti"); //TotiLogger.getLogger("totiServer");
		
		Register register = new Register();
		Link.init(urlPattern, register); // TODO not static ??
		Router router = new Router();
		
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		Set<String> trans = new HashSet<>();
		List<Task> tasks = new LinkedList<>();
		if (translator == null) {
			// this.translator = Translator.create(settings, trans, TotiLogger.getLogger("translator"));
			this.translator = Translator.create(settings, trans, loggerFactory.apply("translator"));
		}
		MapDictionary<UrlPart, Object> mapping = MapDictionary.hashMap();
		for (Module module : modules) {
			TemplateFactory templateFactory = new TemplateFactory(
					tempPath,
					module.getTemplatesPath(),
					module.getName(),
					module.getPath(),
					templateFactories, 
					deleteTempJavaFiles,
					minimalize,
					logger
			);
			templateFactories.put(module.getName(), templateFactory);
			if (module.getTranslationPath() != null) {
				trans.add(module.getTranslationPath());
				trans.add(module.getPath() + "/" + module.getTranslationPath());
			}
			tasks.addAll(
				module.initInstances(env, translator, register, database, loggerFactory.apply(module.getName()))
			);
			LoadUrls.loadUrlMap(mapping, module, router, register);
			module.addRoutes(router);
		};
		AuthenticationCache sessionCache = new AuthenticationCache(tempPath, true, logger);
		ResponseFactory response = new ResponseFactory(
				responseHeaders,
				resourcesPath,
				router,
				templateFactories,
				new TemplateFactory(
					tempPath, "toti/web", "", "", templateFactories,
					deleteTempJavaFiles, minimalize,
					logger
				),
				translator,
				new IdentityFactory(translator, translator.getLocale().getLang()),
				new Authenticator(
					tokenExpirationTime, tokenCustomSalt, 
					sessionCache,
					Hash.getSha256(),
					logger
				),
				new Authorizator(logger),
				charset,
				dirResponseAllowed,
				dirDefaultFile,
				logsPath,
				developIps,
				logger,
				this.initProfiler(logger),
				register,
				mapping
		);
		Server server = Server.createWebServer(
				port,
				threadPool,
				readTimeout,
				response,
				certs,
				maxUploadFileSize,
				charset,
				logger
		);
		return new HttpServer(server, tasks, translator, register, sessionCache);
	}

	private Profiler initProfiler(Logger logger) {
		Profiler profiler = new Profiler();
		profiler.setUse(useProfiler);
		if (useProfiler) {
			logger.warn("Profiler is enabled");
			Database.PROFILER = profiler;
			RestApiServer.PROFILER = profiler;
			LanguageSettings.PROFILER = profiler;
		}
		return profiler;
	}

	/****************************/
	
	public HttpServerFactory setDirResponseAllowed(boolean dirResponseAllowed) {
		this.dirResponseAllowed = dirResponseAllowed;
		return this;
	}
	
	public HttpServerFactory setDirDefaultFile(String dirDefaultFile) {
		this.dirDefaultFile = dirDefaultFile;
		return this;
	}

	public HttpServerFactory setMaxRequestBodySize(int maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
		return this;
	}

	public HttpServerFactory setHeaders(Map<String, List<Object>> headers) {
		this.responseHeaders = new Headers(headers);
		return this;
	}

	public HttpServerFactory setHeaders(Headers headers) {
		this.responseHeaders = headers;
		return this;
	}

	public HttpServerFactory setTranslator(Translator translator) {
		this.translator = translator;
		return this;
	}
/*
	public HttpServerFactory setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}
*/
	
	public HttpServerFactory setLoggerFactory(Function<String, Logger> loggerFactory) {
		this.loggerFactory = loggerFactory;
		return this;
	}
	
	public HttpServerFactory setLogsPath(String logsPath) {
		this.logsPath = logsPath;
		return this;
	}
	
	public HttpServerFactory setPort(int port) {
		this.port = port;
		return this;
	}

	public HttpServerFactory setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}

	public HttpServerFactory setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public HttpServerFactory setCerts(SslCredentials certs) {
		this.certs = Optional.of(certs);
		return this;
	}

	public HttpServerFactory setTempPath(String tempPath) {
		this.tempPath = tempPath;
		return this;
	}

	public HttpServerFactory setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public HttpServerFactory setLanguageSettings(LanguageSettings settings) {
		this.settings = settings;
		return this;
	}
	
	public HttpServerFactory setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
		return this;
	}
	
	public HttpServerFactory setTokenCustomSalt(String tokenCustomSalt) {
		this.tokenCustomSalt = tokenCustomSalt;
		return this;
	}

	public HttpServerFactory setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
		return this;
	}

	public HttpServerFactory setDeleteTempJavaFiles(boolean deleteTempJavaFiles) {
		this.deleteTempJavaFiles = deleteTempJavaFiles;
		return this;
	}

	public HttpServerFactory setMinimalize(boolean minimalize) {
		this.minimalize = minimalize;
		return this;
	}

	public HttpServerFactory setDevelopIpAdresses(List<String> developIps) {
		this.developIps = developIps;
		return this;
	}
	
	public HttpServerFactory setUseProfiler(boolean useProfiler) {
		this.useProfiler = useProfiler;
		return this;
	}

	public HttpServerFactory setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		return this;
	}
	
}
