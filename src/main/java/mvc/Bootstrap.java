package mvc;

import java.io.File;
import java.io.IOException;

import common.Logger;
import mvc.templating.TemplateFactory;
import socketCommunication.Server;
import socketCommunication.http.server.session.FileSessionStorage;
import socketCommunication.http.server.session.SessionStorage;
import translator.Translator;

public class Bootstrap {
	
	private final Server server;

	public Bootstrap(
			int port,
			int threadPool,
    		long readTimeout,
    		long sessionExpirationTime,
    		String tempPath,
    		String templatePath,
    		String[] controllers,
    		String resourcesPath,
    		ResponseHeaders headers,
    		String charset,
    		Translator translator,
    		Logger logger) throws IOException {
		try {
			String cachePath = tempPath + "/cache";
			String sessionPath = tempPath + "/sessions";
			new File(cachePath).mkdir();
			new File(sessionPath).mkdir();
			
		    TemplateFactory templateFactory = new TemplateFactory(cachePath, templatePath);
		    
			ResponseFactory response = new ResponseFactory(
					headers,
					templateFactory,
					translator,
					controllers,
					resourcesPath,
					charset
			);
			
			SessionStorage storage = new FileSessionStorage(sessionPath);
					
			this.server = Server.create(
					port,
					threadPool,
					readTimeout,
					sessionExpirationTime,
					response,
					storage,
					charset,
					logger
			);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void start() {
		server.start();
	}
	
	public void stop() throws InterruptedException {
		server.stop();
	}
	
}
