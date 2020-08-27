package mvc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import common.Logger;
import socketCommunication.Server;
import socketCommunication.http.server.session.FileSessionStorage;
import socketCommunication.http.server.session.SessionStorage;

public class Bootstrap {
	
	private final Server server;

	public Bootstrap(
			int port,
			int threadPool,
    		long readTimeout,
    		long sessionExpirationTime,
    		String tempPath,
    		String templatePath,
    		String resourcesPath,
    		List<String> headers,
    		String charset,
    		Logger logger) throws IOException {
		// TemplateFactory templateFactory = new TemplateFactory(cachePath, templatePath);
		try {
			String cachePath = tempPath + "/cache";
			String sessionPath = tempPath + "/sessions";
			new File(cachePath).mkdir();
			new File(sessionPath).mkdir();
			
			ResponseFactory response = new ResponseFactory(resourcesPath, charset);
			headers.forEach((header)->{
				response.addHeader(header);
			});
			
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
