package toti;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import toti.answers.Answer;
import toti.answers.ControllerAnswer;
import toti.answers.ExceptionAnswer;
import toti.answers.FileSystemAnswer;
import toti.answers.Headers;
import toti.answers.TotiAnswer;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.response.ResponseContainer;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.answers.router.UriPattern;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Param;
import toti.application.register.Register;
import toti.common.structures.ObjectBuilder;
import toti.common.structures.dictionary.Scalar;
import toti.extensions.AuthenticationExtension;
import toti.extensions.Extension;
import toti.extensions.TemplateExtension;
import toti.extensions.Translator;
import toti.extensions.TranslatorExtension;
import toti.files.env.Env;
import toti.extensions.TotiExtension;

public class ApplicationFactory {

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
	
	private List<String> paths;
	private List<String> hostnames;
	
	//private Env appEnv;
	
	private final Env env;
	private final String appIdentifier;
	private final String charset;
	
	private final List<TotiExtension> extensionsTotiResponses;
	
	private final Map<String, Extension> extensions;
	
	private TemplateExtension templateExtension;
	private TranslatorExtension translatorExtension;
	private AuthenticationExtension authenticationExtension;
	
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
	//	Env env = appEnv = this.env.getSection("applications").getModule(hostname);
		// Profiler profiler = initProfiler(env, logger);
		
		
		ObjectBuilder<Module> actualModule = new ObjectBuilder<>();
		Param root = new Param(null);
		Register register = new Register(root, actualModule, pattern, extensions);
		Link link = new Link(/*getUrlPattern(env),*/ register, pattern);
		Router router = new Router(/*register*/);

		extensions.forEach((n, e)->e.init(env, register));
		TranslatorExtension translatorExtension = getTranslatorExtension();
		TemplateExtension templateExtension = getTemplateExtension();
		
		List<Task> tasks = new LinkedList<>();
		
		for (Module module : modules) {
			actualModule.set(module);
			tasks.addAll(module.initInstances(env, register, link));
			module.addRoutes(router, link);
		};
		actualModule.set(null);
		
		IdentityFactory identityFactory = new IdentityFactory(extensions.values());
		
		List<String> developIps = getDevelopIps(env);
		TotiAnswer totiAnwer = new TotiAnswer(
			developIps, templateExtension, translatorExtension, identityFactory, extensionsTotiResponses
		);
		ExceptionAnswer exceptionAnswer = new ExceptionAnswer(
			register,
			developIps,
			getLogsPath(env),
			translatorExtension,
			logger
		);
		ControllerAnswer controllerAnswer = new ControllerAnswer(
			router, root, templateExtension, authenticationExtension,
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

	private String getResourcesPath(Env env) {
		return getProperty(resourcesPath, "resource-path", "www", v->v.getString(), env);
	}
	
	private Boolean getDirResponseAllowed(Env env) {
		return getProperty(dirResponseAllowed, "dir-allowed", false, v->v.getBoolean(), env);
	}
	
	private String getDirDefaultFile(Env env) {
		return getProperty(dirDefaultFile, "dir-default-file", "index.html", v->v.getString(), env);
	}
	
	private List<String> getDevelopIps(Env env) {
		if (developIps != null) {
			return developIps;
		}
		String key = "ip";
		if (env != null && env.getValue(key) != null) {
			return env.getList(key, v->v.getString());
		}
		return Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");
	}
	
	private String getLogsPath(Env env) {
		return getProperty(logsPath, "logs-path", "logs" + "/" + appIdentifier,  v->v.getString(), env);
	}
	
	private Map<String, List<Object>>  getResponseHeaders(Env env) {
		if (responseHeaders != null) {
			return responseHeaders;
		}
		Headers headers = new Headers();
		if (env.getValue("headers") != null) {
			env.getList("headers").forEach(h->{
				String[] hds = h.toString().split(":", 2);
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
	
	private <T> T getProperty(T value,String key, T defaultValue, Function<Scalar, T> get, Env env) {
		if (value != null) {
			return value;
		}
		if (env != null && env.getValue(key) != null) {
			return get.apply(env._getValue(key));
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
		return new TemplateExtension() {
			@Override
			public String getTemplate(String module, String filename, Map<String, Object> params, ResponseContainer container)
				throws Exception {
				throw new RuntimeException("TemplateExtension is not registered");
			}
		};
	}
	
	/*************************/
	
	public ApplicationFactory setUrlPattern(UriPattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public ApplicationFactory addExtension(Extension extension) {
		extensions.put(extension.getClass().getName(), extension);
		
		if (extension instanceof TotiExtension) {
			extensionsTotiResponses.add((TotiExtension)extension);
		}
		if (extension instanceof TranslatorExtension) {
			this.translatorExtension = (TranslatorExtension)extension;
		}
		if (extension instanceof TemplateExtension) {
			this.templateExtension = (TemplateExtension)extension;
		}
		if (extension instanceof AuthenticationExtension) {
			this.authenticationExtension =  (AuthenticationExtension)extension;
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

	public ApplicationFactory setDevelopIpAdresses(List<String> developIps) {
		this.developIps = developIps;
		return this;
	}

	public ApplicationFactory setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}

}
