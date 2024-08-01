package toti;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;

import ji.common.functions.Env;
import ji.common.structures.ThrowingBiFunction;
import toti.http.parsers.Form;
import toti.http.parsers.Payload;
import toti.http.parsers.StreamReader;
import toti.http.parsers.Urlencode;

public class TotiServer {
	
	private final MainHandler handler;
	private final Server server;
	private final Logger logger;
	private final Env env;
	private final String charset;
	
	private final Map<String, Application> applications = new HashMap<>();
	
	private boolean isRunning = false;
	
	public TotiServer(Server server, StreamReader streamReader, Env env, String charset, Logger logger) {
		this.server = server;
		this.logger = logger;
		this.env = env;
		this.charset = charset;
		Payload payload = new Payload();
		this.handler = new MainHandler(new Form(payload, streamReader), new Urlencode(payload, streamReader), streamReader, logger);
		
		// required for websockets
		ContextHandler contextHandler = new ContextHandler();
		/*ServerWebSocketContainer container =*/ ServerWebSocketContainer.ensure(server, contextHandler);
		contextHandler.setHandler(handler);
		
		server.setHandler(contextHandler);
	}
	
	public Application addApplication(
			String appIdentifier,
			ThrowingBiFunction<Env, ApplicationFactory, Application, Exception> init,
			String hostname, String... alias) throws Exception {
		ApplicationFactory applicationFactory = new ApplicationFactory(appIdentifier, env, charset, hostname, alias);
		Application application = init.apply(env, applicationFactory);
		applications.put(appIdentifier, application);
		if (isRunning && application.isAutoStart()) {
			startApplication(appIdentifier);
		}
		return application;
	}
	
	public boolean removeApplication(String appIdentifier) {
		if (applications.containsKey(appIdentifier)) {
			if (!stopApplication(appIdentifier, applications.get(appIdentifier))) {
				return false;
			}
			applications.remove(appIdentifier);
		}
		return true;
	}

	public void start() throws Exception {
		logger.info("Server is starting");
		server.start();
		applications.forEach((host, application)->{
			if (application.isAutoStart()) {
				startApplication(host);
			}
		});
		logger.info("Server is running");
		logger.info("Available applications: " + applications.keySet());
		isRunning = true;
	}
	
	public void stop() throws Exception {
		isRunning = false;
		logger.info("Server is stoping");
		applications.forEach((host, application)->{
			stopApplication(host, application);
		});
		server.stop();
		logger.info("Server stopped");
	}
	
	protected void startApplication(String appIdentifier) {
		if (!applications.containsKey(appIdentifier)) {
			logger.warn("Unknown application: " + appIdentifier);
		}
		try {
			Application application = applications.get(appIdentifier);
			logger.info("Application is starting: " + appIdentifier);
			application.start();
			handler.addApplication(application.getRequestAnswer(), application.getHostname(), application.getAliases());
			logger.info("Application is running: " + appIdentifier);
		} catch (Exception e) {
			logger.error("Application start fail: " + appIdentifier, e);
		}
	}
	
	protected boolean stopApplication(String appIdentifier, Application application) {
		try {
			logger.info("Application is stopping: " + appIdentifier);
			handler.removeApplication(appIdentifier);
			application.stop();
			logger.info("Application is stopped: " + appIdentifier);
			return true;
		} catch (Exception e) {
			logger.error("Application stop fail: " + appIdentifier, e);
			return false;
		}
	}

	/** ONLY FOR TESTS */
	protected Map<String, Application> getApplications() {
		return applications;
	}
	
	/** ONLY FOR TESTS */
	protected void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
}
