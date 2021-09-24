package toti;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import common.Logger;
import common.structures.ThrowingFunction;
import socketCommunication.Server;
import socketCommunication.ServerSecuredCredentials;
import toti.logging.TotiLogger;
import database.Database;
import socketCommunication.http.server.RestApiServer;
import toti.profiler.Profiler;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.IdentityFactory;
import toti.security.User;
import toti.templating.TemplateFactory;
import translator.LanguageSettings;
import translator.LocaleTranslator;
import translator.Translator;
import common.functions.Hash;

public class HttpServer {
	
	public static boolean USE_OLD_IMPL = true;
	
	private final Server server;
	private final ResponseFactory response;
	private final Translator translator;
	private final List<Module> modules;
	
	public <T extends Module> HttpServer(
			int port,
			int threadPool,
    		long readTimeout,
    		ResponseHeaders headers, // TODO only list ??
    		Optional<ServerSecuredCredentials> certs,
    		String tempPath,
    		List<T> modules,
    		ThrowingFunction<String, User, Exception> userFactory,
    		String resourcesPath,
    		Translator translator,
    		int maxUploadFileSize,
    		Optional<List<String>> allowedUploadFileTypes,
    		String charset,
    		LanguageSettings settings,
    		String tokenCustomSalt,
    		long tokenExpiration,
    		Logger logger,
    		boolean deleteDir,
    		boolean dirResponseAllowed,
    		boolean minimalize,
    		List<String> developIps,
    		String redirectNoLoggerdUser,
    		boolean useProfiler) throws Exception {
		Profiler profiler = new Profiler();
		profiler.setUse(useProfiler);
		if (useProfiler) {
			Database.PROFILER = profiler;
			RestApiServer.PROFILER = profiler;
			LocaleTranslator.PROFILER = profiler;
		}
		
		Router router = new Router();
	//	Map<String, TemplateFactory> controllers = new HashMap<>();
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		Set<String> trans = new HashSet<>();
		this.modules = new LinkedList<>();
		for (Module module : modules) {
			this.modules.add(module);
			module.addRoutes(router);
			TemplateFactory templateFactory = new TemplateFactory(
					tempPath,
					module.getTemplatesPath(),
					module.getName(), 
					templateFactories, 
					deleteDir, minimalize,
					logger
			);
			templateFactory.useOldImpl = USE_OLD_IMPL;
		//	modulesToMap.put(module.getControllersPath(), module.getName());
		//	controllers.put(module.getControllersPath(), templateFactory);
			templateFactories.put(module.getName(), templateFactory);
			if (module.getTranslationPath() != null) {
				trans.add(module.getTranslationPath());
			}
		};
		TemplateFactory totiTemplateFactory = new TemplateFactory(
				tempPath, "toti/web", "", templateFactories,
				deleteDir, minimalize,
				logger
		);
		totiTemplateFactory.useOldImpl = USE_OLD_IMPL;
		if (translator == null) {
			translator = new LocaleTranslator(settings, trans, TotiLogger.getLogger("translator"));
			// String[] translators = new String[trans.size()];
			// translator = PropertiesTranslator.create(LoggerFactory.getLogger("translator"), trans.toArray(translators));
		}
	//	AuthenticationCache authenticationCache = new AuthenticationCache(tempPath, false); // TODO enable??
		this.translator = translator;
		this.response = new ResponseFactory(
				headers,
				resourcesPath,
				router,
				// controllers,
				templateFactories,
				totiTemplateFactory,
				translator,
				new IdentityFactory(translator, translator.getLocale().getLang()/*defLang, authenticationCache*/),
				new Authenticator(tokenExpiration, tokenCustomSalt, userFactory, /*authenticationCache,*/ new Hash("SHA-256"), logger),
				new Authorizator(redirectNoLoggerdUser, logger),
				charset,
				dirResponseAllowed,
				developIps,
				logger,
				profiler
		);
				
		this.server = Server.createWebServer(
				port,
				threadPool,
				readTimeout,
				response,
				certs,
				maxUploadFileSize,
				allowedUploadFileTypes,
				charset,
				logger
		);
	}
	
	public Translator getTranslator() {
		return translator;
	}
	
	public void start() throws Exception {
		response.map(modules);
		server.start();
	}
	
	public void stop() throws InterruptedException {
		server.stop();
	}
	
}
