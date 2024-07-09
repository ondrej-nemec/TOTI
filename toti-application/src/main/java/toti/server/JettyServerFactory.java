package toti.server;

import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import ji.common.functions.Env;
import toti.TotiServerFactory;
import toti.http.SslCredentials;

public class JettyServerFactory {
	
	private Integer port = null;
	private Integer threadPool = null;
	private Long readTimeout = null;
	private Optional<SslCredentials> certs = null;
	private Integer maxRequestSize = null;
	private String charset = null;
	
	private final Env env;
	
	public JettyServerFactory(Env env) {
		this.env = env;
	}
	
	public JettyServer create(Logger logger) throws Exception {
		Env settings = env.getModule("http");
		String charset = getCharset(settings);

		Server server = new Server(new QueuedThreadPool(getThreadPool(settings)));
		server.setHandler(new MainHandler(logger));

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(getPort(settings));
		
		server.addConnector(connector);
		
		
		return new JettyServer(server, settings, charset, logger);
	}
	
	/************************/
	
	private int getPort(Env env) {
		return getProperty(port, "port", 80, Integer.class, env);
	}
	
	private String getCharset(Env env) {
		return getProperty(charset, "charset", "UTF-8", String.class, env);
	}

	private int getThreadPool(Env env) {
		return getProperty(threadPool, "thread-pool", 5, Integer.class, env);
	}
	
	private <T> T getProperty(T value, String key, T defaultValue, Class<T> clazz, Env env) {
		if (value != null) {
			return value;
		}
		if (env != null && env.getValue(key) != null) {
			return env.getDictionaryValue(key).getValue(clazz);
		}
		return defaultValue;
	}
	
	/****************************/

	public JettyServerFactory setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}

	public JettyServerFactory setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public JettyServerFactory setPort(int port) {
		this.port = port;
		return this;
	}
	
}
