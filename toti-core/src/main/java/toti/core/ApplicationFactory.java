package toti.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import toti.core.answers.Answer;
import toti.core.answers.ControllerAnswer;
import toti.core.answers.ExceptionAnswer;
import toti.core.answers.FileSystemAnswer;
import toti.core.answers.Headers;
import toti.core.answers.TotiAnswer;
import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.answers.router.UriPattern;
import toti.core.answers.session.Identity;
import toti.core.answers.session.IdentityFactory;
import toti.core.answers.session.SessionManager;
import toti.core.application.Module;
import toti.core.application.Task;
import toti.core.application.register.Param;
import toti.core.application.register.Register;
import toti.core.extensions.Extension;
import toti.core.extensions.TemplateFactory;
import toti.core.extensions.TotiExtension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.structures.dictionary.Scalar;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;

public class ApplicationFactory {

	private String resourcesPath = null;
	private Boolean dirResponseAllowed = null;
	private String dirDefaultFile = null;
	private Function<String, Boolean> isDevelop = null;
	private String logsPath = null;
	
	private Boolean autoStart = null;

	private Map<String, List<Object>>  responseHeaders = null;
	private UriPattern pattern = new UriPattern() {};
	
	private final String basePath;
	private final String hostname;
	
	private final Env env;
	private final String appIdentifier;
	private final String charset;
	
	private final List<TotiExtension> extensionsTotiResponses;
	
	private final Map<String, Extension> extensions;
	private SessionManager sessionManager;

	private TemplateFactory templateExtension;
	
	public ApplicationFactory(String appIdentifier, Env env, String charset, String hostname, String basePath) {
		this.env = env;
		this.appIdentifier = appIdentifier;
		this.charset = charset;
		this.basePath = basePath;
		this.hostname = hostname;
		
		this.extensions = new HashMap<>();
		this.extensionsTotiResponses = new LinkedList<>();
	}

	public Application create(List<Module> modules, Logger logger) {
		ObjectBuilder<Module> actualModule = new ObjectBuilder<>();
		Param root = new Param(null);
		Register register = new Register(root, actualModule, pattern, extensions);
		Link link = new Link(basePath == null ? "" : "/" + basePath, /*getUrlPattern(env),*/ register, pattern);
		Router router = new Router(/*register*/);

		extensions.forEach((n, e)->e.init(env, register));
		TemplateFactory actualTemplateExtension = getTemplateFactory();
		
		List<Task> tasks = new LinkedList<>();
		
		for (Module module : modules) {
			actualModule.set(module);
			tasks.addAll(module.init(env, register, link, router));
		}
		actualModule.set(null);
		
		SessionManager currentSessionManager = getSessionManager();
		IdentityFactory identityFactory = new IdentityFactory(extensions.values(), currentSessionManager);
		addExtension(new Extension() {
			@Override public String getIdentifier() { return "toti_sessionManager"; }
			@Override public void init(Env appEnv, Register register) {}
			@Override public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody) {}
			@Override public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}
			@Override public void onApplicationStart() throws Exception { sessionManager.onApplicationStart(); }
			@Override public void onApplicationStop() throws Exception { sessionManager.onApplicationStop(); }
		});
		
		Function<String, Boolean> isDevelopFunc = getDevModeFunc();
		
		TotiAnswer totiAnwer = new TotiAnswer(
			isDevelopFunc, actualTemplateExtension, extensionsTotiResponses
		);
		ExceptionAnswer exceptionAnswer = new ExceptionAnswer(register, isDevelopFunc, getLogsPath(env), logger);
		ControllerAnswer controllerAnswer = new ControllerAnswer(router, root, actualTemplateExtension, link, logger);
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
			answer, getAutoStart(env), hostname, basePath
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
				if (h.isSection()) {
					h.getSection().getList("header").forEach(v->{
						parseHeader(headers, v.getValue().toString());
					});
				} else if (h.isValue()) {
					parseHeader(headers, h.getValue().toString());
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

	private void parseHeader(Headers headers, String header) {
		String[] hds = header.split(":", 2);
		if (hds.length == 1) {
			headers.addHeader(hds[0].trim(), "");
		} else {
			headers.addHeader(hds[0].trim(), hds[1].trim());
		}
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
