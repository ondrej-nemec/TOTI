package toti;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.socketCommunication.SslCredentials;
import ji.socketCommunication.http.RestApiServer;
import toti.logging.TotiLoggerFactory;

public class HttpServerFactory {
	
	private Integer port = null;
	private Integer threadPool = null;
	private Long readTimeout = null;
	private Optional<SslCredentials> certs = null;
	private Integer maxRequestSize = null;
	private String charset = null;
	
	private final Env env;
	
	private Function<String, Logger> loggerFactory = TotiLoggerFactory.get();
	
	public HttpServerFactory() {
		this.env = new Env(new Properties());
	}
	
	public HttpServerFactory(String configFile) throws FileNotFoundException, IOException {
		this(new Env(configFile));
	}

	public HttpServerFactory(Env env) {
		this.env = env;
	}
	
	public  HttpServer create() throws Exception {
		// maybe more - separated - loggers??
		Logger logger = loggerFactory.apply("toti"); //TotiLogger.getLogger("totiServer");
		Env settings = env.getModule("http");
		RestApiServer server = new RestApiServer(getMaxRequestSize(settings), logger);
		// TODO add aplication from env
		String charset = getCharset(settings);
		return new HttpServer(
			server.createWebServer(
				getPort(settings), getThreadPool(settings), getReadTimeout(settings),
				getCerts(settings), charset
			),
			env,
			charset,
			(responseFactory, hostName)->server.addApplication(responseFactory, hostName),
			(hostName)->server.removeApplication(hostName),
			loggerFactory,
			logger
		);
	}
	
	private int getPort(Env env) {
		return getProperty(port, "port", 80, Integer.class, env);
	}
	
	private int getThreadPool(Env env) {
		return getProperty(threadPool, "port", 5, Integer.class, env);
	}
	
	private long getReadTimeout(Env env) {
		return getProperty(readTimeout, "read-timeout", 60000L, Long.class, env);
	}
	
	private Integer getMaxRequestSize(Env env) {
		return getProperty(maxRequestSize, "max-request-size", null, Integer.class, env);
	}
	
	private String getCharset(Env env) {
		return getProperty(charset, "charset", "UTF-8", String.class, env);
	}
	
	private Optional<SslCredentials> getCerts(Env env) {
		if (certs != null) {
			return certs;
		}
		if (env != null) {
			SslCredentials cred = new SslCredentials();
			if (env.getString("key-store") != null && env.getString("key-store-password") != null) {
				cred.setCertificateStore(
					env.getString("key-store"),
					env.getString("key-store-password")
				);
			}
			if (env.getString("trust-store") != null && env.getString("trust-store-password") != null) {
				cred.setTrustedClientsStore(
					env.getString("trust-store"),
					env.getString("trust-store-password")
				);
			} else {
				cred.setTrustAll(true);
			}
			if (env.getString("trust-store") != null || env.getString("key-store") != null) {
				return Optional.of(cred);
			}
		}
		return Optional.empty();
	}
	
	private <T> T getProperty(T value,String key, T defaultValue, Class<T> clazz, Env env) {
		if (value != null) {
			return value;
		}
		if (env != null && env.getValue(key) != null) {
			return env.getDictionaryValue(key).getValue(clazz);
		}
		return defaultValue;
	}
	
	/*********************/
	
	public HttpServerFactory setMaxRequestBodySize(int maxRequestBodySize) {
		this.maxRequestSize = maxRequestBodySize;
		return this;
	}

	public HttpServerFactory setLoggerFactory(Function<String, Logger> loggerFactory) {
		this.loggerFactory = loggerFactory;
		return this;
	}

	public HttpServerFactory setPort(int port) {
		this.port = port;
		return this;
	}

	public HttpServerFactory setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}

	public HttpServerFactory setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public HttpServerFactory setCerts(SslCredentials certs) {
		this.certs = Optional.of(certs);
		return this;
	}

	public HttpServerFactory setCharset(String charset) {
		this.charset = charset;
		return this;
	}

}
