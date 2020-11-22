package toti;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import socketCommunication.Server;
import socketCommunication.ServerSecuredCredentials;
import toti.authentication.Authenticator;
import toti.authentication.Identity;
import toti.authentication.Language;
import toti.registr.Registr;
import toti.templating.TemplateFactory;
import translator.Translator;

public class Bootstrap {
	
	private final Server server;
	
	public Bootstrap(
			int port,
			int threadPool,
    		long readTimeout,
    		ResponseHeaders headers, // TODO only list ??
    		Optional<ServerSecuredCredentials> certs,
    		String tempPath,
    		List<Module> modules,
    		String resourcesPath,
    		Function<Locale, Translator> translator,
    		AuthorizationHelper authorizator,
    		Function<Identity, AclUser> identityToUser,
    		int maxUploadFileSize,
    		Optional<List<String>> allowedUploadFileTypes,
    		String charset,
    		String defLang,
    		String tokenSalt,
    		long tokenExpirationTime,
    		Logger logger,
    		Logger securityLogger,
    		boolean deleteDir,
    		boolean dirResponseAllowed) throws Exception {

		Authenticator authenticator = new Authenticator(
				tokenExpirationTime,
				tokenSalt,
				securityLogger
		);
		Registr registr = Registr.get();
		Router router = new Router();
		
		Map<String, TemplateFactory> controllers = new HashMap<>();
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		modules.forEach((module)->{
			module.addRoutes(router);
			module.initInstances(registr);
			TemplateFactory templateFactory = new TemplateFactory(
					tempPath, module.getTemplatesPath(), templateFactories, deleteDir
			);
			controllers.put(module.getControllersPath(), templateFactory);
			templateFactories.put(module.getModuleName(), templateFactory);
		});

		ResponseFactory response = new ResponseFactory(
				headers,
				new Language(defLang),
				resourcesPath,
				router,
				controllers,
				translator,
				authenticator,
				authorizator,
				identityToUser,
				charset,
				dirResponseAllowed,
				logger
		);
				
		this.server = Server.create(
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
	
	public void start() {
		server.start();
	}
	
	public void stop() throws InterruptedException {
		server.stop();
	}
	
}
