package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.structures.ObjectBuilder;
import toti.answers.Answer;
import toti.answers.ControllerAnswer;
import toti.answers.ExceptionAnswer;
import toti.answers.FileSystemAnswer;
import toti.answers.Headers;
import toti.answers.TotiAnswer;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.answers.router.UriPattern;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Param;
import toti.application.register.Register;
import toti.extensions.Extension;
import toti.extensions.TemplateExtension;
import toti.extensions.Translator;
import toti.extensions.TranslatorExtension;
import toti.extensions.OnTotiExtension;

public class ApplicationFactory {
	
	private String tempPath = null;
	private String resourcesPath = null;
	private Boolean dirResponseAllowed = null;
	private String dirDefaultFile = null;
	private List<String> developIps = null;
//	private Long tokenExpirationTime = null;
//	private String tokenCustomSalt = null;
	// private String urlPattern = null;
	private String logsPath = null;
	
	private Boolean autoStart = null;

	private Map<String, List<Object>>  responseHeaders = null;
	private UriPattern pattern = new UriPattern() {};
	
	private String[] aliases;
	private String hostname;
	
	//private Env appEnv;
	
	private final Env env;
	private BiFunction<String, String, Logger> loggerFactory;
	private final String appIdentifier;
	private final String charset;
	
	private final List<OnTotiExtension> extensionsTotiResponses;
	
	private final List<Extension> extensions;
	
	private TemplateExtension templateExtension;
	private TranslatorExtension translatorExtension;
	
	public ApplicationFactory(String appIdentifier, Env env, String charset, Function<String, Logger> loggerFactory,
		String hostname, String... aliases) {
		this.env = env.getModule("applications").getModule(appIdentifier);
		this.loggerFactory = (hostName, loggerName)->loggerFactory.apply(appIdentifier+ "_" + loggerName);
		this.appIdentifier = appIdentifier;
		this.charset = charset;
		this.aliases = aliases;
		this.hostname = hostname;
		
		this.extensions = new LinkedList<>();
		this.extensionsTotiResponses = new LinkedList<>();
	}

	public Application create(List<Module> modules) throws Exception {
		Logger logger = loggerFactory.apply(appIdentifier, "toti");
		
	//	Env env = appEnv = this.env.getModule("applications").getModule(hostname);
		// Profiler profiler = initProfiler(env, logger);
		
		
		ObjectBuilder<Module> actualModule = new ObjectBuilder<>();
		Param root = new Param(null);
		Register register = new Register(root, actualModule, pattern);
		Link link = new Link(/*getUrlPattern(env),*/ register, pattern);
		Router router = new Router(/*register*/);

		extensions.forEach((e)->e.init(env, register));
		TranslatorExtension translatorExtension = getTranslatorExtension();
		TemplateExtension templateExtension = getTemplateExtension();
		
		List<Task> tasks = new LinkedList<>();
		
		for (Module module : modules) {
			actualModule.set(module);
			tasks.addAll(module.initInstances(env, register, link));
			module.addRoutes(router, link);
		};
		actualModule.set(null);
		
		IdentityFactory identityFactory = new IdentityFactory(extensions, register.getSessionUserProvider());
		
		List<String> developIps = getDevelopIps(env);
		TotiAnswer totiAnwer = new TotiAnswer(
			developIps, templateExtension, identityFactory, extensionsTotiResponses
		);
		ExceptionAnswer exceptionAnswer = new ExceptionAnswer(
			register,
			developIps,
			getLogsPath(env),
			translatorExtension,
			logger
		);
		ControllerAnswer controllerAnswer = new ControllerAnswer(
			router, root, templateExtension, register.getSessionUserProvider(),
			identityFactory, link, translatorExtension, logger
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
			tasks, root, link, register, extensions,
			answer, getAutoStart(env), hostname, aliases
		);
	}

	/*private Profiler initProfiler(Env env, Logger logger) {
		if (getUseProfiler(env) && profiler != null) {
			logger.warn("Profiler is enabled");
			return profiler;
		}
		if (getUseProfiler(env) && profiler == null) {
			logger.warn("Profiler is enabled but no profiler set.");
		}
		return Profiler.empty();
	}*/
		
	/*************************/
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
	
	private Boolean getDirResponseAllowed(Env env) {
		return getProperty(dirResponseAllowed, "dir-allowed", false, Boolean.class, env);
	}
	
	private String getDirDefaultFile(Env env) {
		return getProperty(dirDefaultFile, "dir-default-file", "index.html", String.class, env);
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
	
	private String getLogsPath(Env env) {
		return getProperty(logsPath, "logs-path", "logs" + "/" + appIdentifier,  String.class, env);
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
	
	private TranslatorExtension getTranslatorExtension() {
		if (translatorExtension != null) {
			return translatorExtension;
		}
		return new TranslatorExtension() {
			public Translator getTranslator(Identity identity) {
				return new Translator() {
					@Override public String translate(String key) { return key; }
					@Override public String translate(String key, Map<String, Object> params) { return key; }
				};
			};
		};
	}
	
	private TemplateExtension getTemplateExtension() {
		if (templateExtension != null) {
			return templateExtension;
		}
		return null; // TODO
	}
	
	/*************************/
	
	public ApplicationFactory setUrlPattern(UriPattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public ApplicationFactory addExtension(Extension extension) {
		extensions.add(extension);
		
		if (extension instanceof OnTotiExtension) {
			extensionsTotiResponses.add((OnTotiExtension)extension);
		}
		if (extension instanceof TranslatorExtension) {
			this.translatorExtension = (TranslatorExtension)extension;
		}
		if (extension instanceof TemplateExtension) {
			this.templateExtension = (TemplateExtension)extension;
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

	public ApplicationFactory setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
		return this;
	}

	public ApplicationFactory setDevelopIpAdresses(List<String> developIps) {
		this.developIps = developIps;
		return this;
	}

	public ApplicationFactory setLoggerFactory(BiFunction<String, String, Logger> loggerFactory) {
		this.loggerFactory = loggerFactory;
		return this;
	}

	public ApplicationFactory setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}

}
