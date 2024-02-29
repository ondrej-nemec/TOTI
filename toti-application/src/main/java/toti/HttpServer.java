package toti;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.structures.ThrowingBiFunction;
import ji.socketCommunication.Server;

public class HttpServer {
	
	private final Server server;
	private final Env env;
	private final ServerConsumer consumer;
	
	private final Function<String, Logger> loggerFactory;
	private final Logger logger;
	private final String charset;
	
	private final Map<String, Application> applications = new HashMap<>();
	
	private boolean isRunning = false;
	
	protected HttpServer(
			Server server, Env env, String charset,
			ServerConsumer consumer,
			Function<String, Logger> loggerFactory, Logger logger) {
		this.server = server;
		this.env = env;
		this.consumer = consumer;
		this.loggerFactory = loggerFactory;
		this.logger = logger;
		this.charset = charset;
	}
	
	public Application addApplication(
			String appIdentifier,
			ThrowingBiFunction<Env, ApplicationFactory, Application, Exception> init,
			String... alias) throws Exception {
		ApplicationFactory applicationFactory = new ApplicationFactory(appIdentifier, env, charset, loggerFactory, alias);
		Application application = init.apply(env, applicationFactory);
		applications.put(appIdentifier, application);
		if (isRunning && application.isAutoStart()) {
			startApplication(appIdentifier, application);
		}
		return application;
	}
	
	public boolean removeApplication(String appIdentifier) {
		if (applications.containsKey(appIdentifier)) {
			if (!stopApplication(appIdentifier, applications.get(appIdentifier))) {
				return false;
			}
			//consumer.removeApplication(hostname);
			applications.remove(appIdentifier);
		}
		return true;
	}

	public void start() throws Exception {
		logger.info("Server is starting");
		server.start();
		applications.forEach((host, application)->{
			if (application.isAutoStart()) {
				startApplication(host, application);
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
	
	protected void startApplication(String appIdentifier, Application application) {
		try {
			logger.info("Application is starting: " + appIdentifier);
			application.start();
			consumer.addApplication(application.getRequestAnswer(), appIdentifier, application.getAliases());
			logger.info("Application is running: " + appIdentifier);
		} catch (Exception e) {
			logger.error("Application start fail: " + appIdentifier, e);
		}
	}
	
	protected boolean stopApplication(String appIdentifier, Application application) {
		try {
			logger.info("Application is stopping: " + appIdentifier);
			consumer.removeApplication(appIdentifier);
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
