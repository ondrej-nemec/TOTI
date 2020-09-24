package mvc;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import mvc.authentication.Authenticator;
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
    		long sessionExpirationTime,
    		String tempPath,
    		Map<String, String> folders,
    		String resourcesPath,
    		ResponseHeaders headers,
    		Optional<ServerSecuredCredentials> certs,
    		String charset,
    		Function<Locale, Translator> translator,
    		Authenticator authenticator,
    		Router router,
    		Logger logger) throws Exception {
		this(
				port, threadPool, readTimeout, sessionExpirationTime,
				tempPath, folders, 
				resourcesPath,headers, certs,
				charset, translator, authenticator, router, logger, true);
	}
	
	public Bootstrap(
			int port,
			int threadPool,
    		long readTimeout,
    		long sessionExpirationTime,
    		String tempPath,
    		Map<String, String> folders,
    		String resourcesPath,
    		ResponseHeaders headers,
    		Optional<ServerSecuredCredentials> certs,
    		String charset,
    		Function<Locale, Translator> translator,
    		Authenticator authenticator,
    		Router router,
    		Logger logger,
    		boolean deleteDir) throws Exception {
		TemplateFactory templateFactory = new TemplateFactory(tempPath,/* templatePath,*/ deleteDir);
		
		ResponseFactory response = new ResponseFactory(
				headers,
				templateFactory,
				router,
				translator,
				null, // authorizator
				authenticator,
				folders,
				resourcesPath,
				charset
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
