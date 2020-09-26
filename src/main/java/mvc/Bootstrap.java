package mvc;

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
			int port, // settins section
			int threadPool, // settins section
    		long readTimeout, // settins section
    		ResponseHeaders headers, // settins section
    		Optional<ServerSecuredCredentials> certs, // settins section
    		String tempPath, // settins section
    		Map<String, String> folders,
    		String resourcesPath, // settins section
    		Router router,
    		Function<Locale, Translator> translator,
    		AuthorizationHelper authorizator, // secured section
    		Function<Identity, AclUser> identityToUser, // secured section
    		String charset, // settins section
    		String defLang, // settings section
    		String tokenSalt,
    		long tokenExpirationTime,
    		Logger logger,
    		Logger securityLogger) throws Exception {
		this(
				port, threadPool, readTimeout,
				headers, certs,
				tempPath, folders, resourcesPath, router, translator, 
				authorizator, identityToUser,
				charset, defLang, tokenSalt, tokenExpirationTime, logger, securityLogger, true);
		
	}
	
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
		
		TemplateFactory templateFactory = new TemplateFactory(tempPath, deleteDir);
		
		ResponseFactory response = new ResponseFactory(
				headers,
				resourcesPath,
				router,
				folders,
				templateFactory,
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
