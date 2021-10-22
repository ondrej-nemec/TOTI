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
import translator.Translator;
import common.functions.Hash;

public class HttpServer {
	
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
    		boolean useProfiler) throws Exception {
		Profiler profiler = new Profiler();
		profiler.setUse(useProfiler);
		if (useProfiler) {
			Database.PROFILER = profiler;
			RestApiServer.PROFILER = profiler;
			LanguageSettings.PROFILER = profiler;
		}
		
		Router router = new Router();
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		Set<String> trans = new HashSet<>();
		this.modules = new LinkedList<>();
		for (Module module : modules) {
			this.modules.add(module);
		//	module.addRoutes(router);
			TemplateFactory templateFactory = new TemplateFactory(
					tempPath,
					module.getTemplatesPath(),
					module.getName(), 
					templateFactories, 
					deleteDir, minimalize,
					logger
			);
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
		if (translator == null) {
			translator = Translator.create(settings, trans, TotiLogger.getLogger("translator"));
		}
	//	AuthenticationCache authenticationCache = new AuthenticationCache(tempPath, false); // TODO enable??
		this.translator = translator;
		this.response = new ResponseFactory(
				headers,
				resourcesPath,
				router,
				templateFactories,
				totiTemplateFactory,
				translator,
				new IdentityFactory(translator, translator.getLocale().getLang()/*defLang, authenticationCache*/),
				new Authenticator(tokenExpiration, tokenCustomSalt, userFactory, /*authenticationCache,*/ new Hash("SHA-256"), logger),
				new Authorizator(logger),
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
