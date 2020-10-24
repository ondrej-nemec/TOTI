package mvc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import mvc.authentication.Authenticator;
import mvc.authentication.Identity;
import mvc.templating.TemplateFactory;
import socketCommunication.Server;
import socketCommunication.ServerSecuredCredentials;
import translator.Translator;

public class Bootstrap {
	
	private final Server server;
	
	public Bootstrap(
			int port,
			int threadPool,
    		long readTimeout,
    		ResponseHeaders headers,
    		Optional<ServerSecuredCredentials> certs,
    		String tempPath,
    		Map<String, String> folders,
    		String resourcesPath,
    		Router router,
    		Function<Locale, Translator> translator,
    		AuthorizationHelper authorizator,
    		Function<Identity, AclUser> identityToUser,
    		String charset,
    		String defLang,
    		String tokenSalt,
    		long tokenExpirationTime,
    		Logger logger,
    		Logger securityLogger,
    		boolean deleteDir) throws Exception {

		Authenticator authenticator = new Authenticator(
				tokenExpirationTime,
				tokenSalt,
				securityLogger
		);
		Map<String, TemplateFactory> modules = new HashMap<>();
		folders.forEach((controller, templates)->{
			modules.put(controller, new TemplateFactory(tempPath, templates, modules, deleteDir));
		});
		
		ResponseFactory response = new ResponseFactory(
				headers,
				resourcesPath,
				router,
				modules,
				translator,
				authenticator,
				authorizator,
				identityToUser,
				charset,
				defLang,
				logger
		);
				
		this.server = Server.create(
				port,
				threadPool,
				readTimeout,
				response,
				certs,
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
