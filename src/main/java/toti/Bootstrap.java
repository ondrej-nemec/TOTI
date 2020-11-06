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
    		Map<String, String> folders,
    		String resourcesPath,
    		Router router,
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
		Map<String, TemplateFactory> modules = new HashMap<>();
		folders.forEach((controller, templates)->{
			modules.put(controller, new TemplateFactory(tempPath, templates, modules, deleteDir));
		});
		
		ResponseFactory response = new ResponseFactory(
				headers,
				new Language(defLang),
				resourcesPath,
				router,
				modules,
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
