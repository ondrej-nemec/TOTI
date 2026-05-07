package toti.application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import toti.application.answers.Answer;
import toti.application.answers.ControllerAnswer;
import toti.application.answers.ExceptionAnswer;
import toti.application.answers.FileSystemAnswer;
import toti.application.answers.Headers;
import toti.application.answers.TotiAnswer;
import toti.application.answers.request.Identity;
import toti.application.answers.request.IdentityFactory;
import toti.application.answers.router.Link;
import toti.application.answers.router.Router;
import toti.application.answers.router.UriPattern;
import toti.application.answers.session.SessionManager;
import toti.application.application.Module;
import toti.application.application.Task;
import toti.application.application.register.Param;
import toti.application.application.register.Register;
import toti.application.extensions.Extension;
import toti.application.extensions.TemplateFactory;
import toti.application.extensions.Translator;
import toti.application.extensions.TranslatorExtension;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.structures.dictionary.Scalar;
import toti.lib.files.env.Env;
import toti.application.extensions.TotiExtension;

public class ApplicationFactory {

	private String resourcesPath = null;
	private Boolean dirResponseAllowed = null;
	private String dirDefaultFile = null;
	private Function<String, Boolean> isDevelop = null;
	private String logsPath = null;
	
	private Boolean autoStart = null;

	private Map<String, List<Object>>  responseHeaders = null;
	private UriPattern pattern = new UriPattern() {};
	
	private final List<String> paths;
	private final List<String> hostnames;
	
	private final Env env;
	private final String appIdentifier;
	private final String charset;
	
	private final List<TotiExtension> extensionsTotiResponses;
	
	private final Map<String, Extension> extensions;
	private SessionManager sessionManager;

	private TemplateFactory templateExtension;
	private TranslatorExtension translatorExtension;
	
	public ApplicationFactory(String appIdentifier, Env env, String charset, List<String> hostnames, List<String> paths) {
		this.env = env;
		this.appIdentifier = appIdentifier;
		this.charset = charset;
		this.paths = paths;
		this.hostnames = hostnames;
		
		this.extensions = new HashMap<>();
		this.extensionsTotiResponses = new LinkedList<>();
	}

	public Application create(List<Module> modules, Logger logger) throws Exception {
		ObjectBuilder<Module> actualModule = new ObjectBuilder<>();
		Param root = new Param(null);
		Register register = new Register(root, actualModule, pattern, extensions);
		Link link = new Link(/*getUrlPattern(env),*/ register, pattern);
		Router router = new Router(/*register*/);

		extensions.forEach((n, e)->e.init(env, register));
		TranslatorExtension translatorExtension = getTranslatorExtension();
		TemplateFactory templateExtension = getTemplateFactory();
		
		List<Task> tasks = new LinkedList<>();
		
		for (Module module : modules) {
			actualModule.set(module);
			tasks.addAll(module.initInstances(env, register, link));
			module.addRoutes(router, link);
		}
		actualModule.set(null);
		
		IdentityFactory identityFactory = new IdentityFactory(extensions.values(), getSessionManager());
		
		Function<String, Boolean> isDevelopFunc = getDevModeFunc();
		
		TotiAnswer totiAnwer = new TotiAnswer(
			isDevelopFunc, templateExtension, translatorExtension, extensionsTotiResponses
		);
		ExceptionAnswer exceptionAnswer = new ExceptionAnswer(register, isDevelopFunc, getLogsPath(env), translatorExtension, logger);
		ControllerAnswer controllerAnswer = new ControllerAnswer(
			router, root, templateExtension,
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
			tasks, root, link, register, extensions.values(),
			answer, getAutoStart(env), hostnames, paths
		);
	}

	/*************************/

	private String getResourcesPath(Env env) {
		return getProperty(resourcesPath, "resource-path", "www", v->v.getString(), env);
	}
	
	private Boolean getDirResponseAllowed(Env env) {
		return getProperty(dirResponseAllowed, "dir-allowed", false, v->v.getBoolean(), env);
	}
	
	private String getDirDefaultFile(Env env) {
		return getProperty(dirDefaultFile, "dir-default-file", "index.html", v->v.getString(), env);
	}
	
	private Function<String, Boolean> getDevModeFunc() {
		if (isDevelop == null) {
			return ip->ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1");
		}
		return isDevelop;
	}
	
	private String getLogsPath(Env env) {
		return getProperty(logsPath, "logs-path", "logs" + "/" + appIdentifier,  v->v.getString(), env);
	}
	
	private Map<String, List<Object>>  getResponseHeaders(Env env) {
		if (responseHeaders != null) {
			return responseHeaders;
		}
		Headers headers = new Headers();
		if (!env.getList("headers").isEmpty()) {
			env.getList("headers").forEach(h->{
				String header = "";
				if (h.isSection()) {
					header = h.getSection().getString("header");
				} else if (h.isValue()) {
					header = h.getValue().toString();
				}
				String[] hds = header.split(":", 2);
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
		return getProperty(autoStart, "autostart", true, v->v.getBoolean(), env);
	}
	
	private <T> T getProperty(T value, String key, T defaultValue, Function<Scalar, T> get, Env env) {
		if (value != null) {
			return value;
		}
		if (env.getValue(key) != null) {
			return get.apply(env._getValue(key));
		}
		return defaultValue;
	}
	
	/*************************/

	private SessionManager getSessionManager() {
		if (sessionManager == null) {
			return SessionManager.empty();
		}
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
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
	
	private TemplateFactory getTemplateFactory() {
		if (templateExtension != null) {
			return templateExtension;
		}
		return (module, filename, params, container)-> {
			throw new RuntimeException("TemplateExtension is not registered");
		};
	}
	
	/*************************/
	
	public ApplicationFactory setUrlPattern(UriPattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public ApplicationFactory addExtension(Extension extension) {
		extensions.put(extension.getClass().getName(), extension);
		
		if (extension instanceof TotiExtension ext) {
			extensionsTotiResponses.add(ext);
		}
		if (extension instanceof TranslatorExtension ext) {
			this.translatorExtension = ext;
		}
		if (extension instanceof TemplateFactory ext) {
			this.templateExtension = ext;
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

	public ApplicationFactory setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
		return this;
	}

	public ApplicationFactory setDevModeFunc(Function<String, Boolean> isDevelop) {
		this.isDevelop = isDevelop;
		return this;
	}

	public ApplicationFactory setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}

}
