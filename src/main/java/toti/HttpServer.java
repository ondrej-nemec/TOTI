package toti;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ji.common.Logger;
import ji.socketCommunication.Server;
import ji.socketCommunication.SslCredentials;
import toti.logging.TotiLogger;
import ji.database.Database;
import ji.socketCommunication.http.server.RestApiServer;
import toti.profiler.Profiler;
import toti.registr.Register;
import toti.security.AuthenticationCache;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.IdentityFactory;
import toti.templating.TemplateFactory;
import toti.url.Link;
import ji.translator.LanguageSettings;
import ji.translator.Translator;
import ji.common.functions.Hash;

public class HttpServer {
	
	private final Server server;
	private final ResponseFactory response;
	private final Translator translator;
	private final List<Module> modules;
	private final Register register;
	
	public <T extends Module> HttpServer(
			int port,
			int threadPool,
    		long readTimeout,
    		ResponseHeaders headers, // TODO only list ??
    		String urlPattern,
    		Optional<SslCredentials> certs,
    		String tempPath,
    		List<T> modules,
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
		this.register = new Register();
		Link.init(urlPattern, register); // TODO not static ??
		
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
		AuthenticationCache authenticationCache = new AuthenticationCache(tempPath, false, logger); // TODO enable??
		this.translator = translator;
		this.response = new ResponseFactory(
				headers,
				resourcesPath,
				router,
				templateFactories,
				totiTemplateFactory,
				translator,
				new IdentityFactory(translator, translator.getLocale().getLang()/*defLang, authenticationCache*/),
				new Authenticator(tokenExpiration, tokenCustomSalt, authenticationCache, new Hash("SHA-256"), logger),
				new Authorizator(logger),
				charset,
				dirResponseAllowed,
				developIps,
				logger,
				profiler,
				register
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
	
	public Register getRegister() {
		return register;
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
