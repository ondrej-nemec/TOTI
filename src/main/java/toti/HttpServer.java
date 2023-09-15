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
	
	protected  HttpServer(
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
			String hostname,
			ThrowingBiFunction<Env, ApplicationFactory, Application, Exception> init,
			String... alias) throws Exception {
		ApplicationFactory applicationFactory = new ApplicationFactory(hostname, env, charset, loggerFactory, alias);
		Application application = init.apply(env, applicationFactory);
		applications.put(hostname, application);
		if (isRunning && application.isAutoStart()) {
			startApplication(hostname, application);
		}
		return application;
	}
	
	public boolean removeApplication(String hostname) {
		if (applications.containsKey(hostname)) {
			if (!stopApplication(hostname, applications.get(hostname))) {
				return false;
			}
			//consumer.removeApplication(hostname);
			applications.remove(hostname);
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
	
	private void startApplication(String host, Application application) {
		try {
			logger.info("Application is starting: " + host);
			application.start();
			consumer.addApplication(application.getRequestAnswer(), host, application.getAliases());
			logger.info("Application is running: " + host);
		} catch (Exception e) {
			logger.error("Application start fail: " + host, e);
		}
	}
	
	private boolean stopApplication(String host, Application application) {
		try {
			logger.info("Application is stopping: " + host);
			consumer.removeApplication(host);
			application.stop();
			logger.info("Application is stopped: " + host);
			return true;
		} catch (Exception e) {
			logger.error("Application stop fail: " + host, e);
			return false;
		}
	}
	
}
