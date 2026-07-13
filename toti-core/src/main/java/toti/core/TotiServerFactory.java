package toti.core;

import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import toti.lib.common.structures.dictionary.Scalar;
import toti.lib.files.env.Env;
import toti.lib.tcpip.SslCredentials;
import toti.lib.tcpip.parsers.StreamReader;

public class TotiServerFactory {
	
	private Integer httpsPort = null;
	private Integer httpPort = null;
	private Integer threadPool = null;
	private Long readTimeout = null;
	private Optional<SslCredentials> certs = null;
	private Integer maxRequestSize = null;
	private String charset = null;
	
	private final Env env;
	
	public TotiServerFactory() {
		this.env = Env.empty();
	}
	
	public TotiServerFactory(String configFile) throws Exception {
		this(Env.load(configFile));
	}

	public TotiServerFactory(Env env) {
		this.env = env;
	}
	
	public Env getEnv() {
		return env;
	}
	
	public TotiServer create(Logger logger) throws Exception {
		Env settings = env.getSection("http");
		String charset = getCharset(settings);

		Server server = new Server(new QueuedThreadPool(getThreadPool(settings)));
		
		Optional<SslCredentials> certs = getCerts(settings);
		long readTimeout = getReadTimeout(settings);
		
		int httpPort = getHttpPort(settings);
		if (httpPort > 0) {
			server.addConnector(createHTTP(server, httpPort, readTimeout));
		}
		int httpsPort = getHttpsPort(settings);
		if (certs.isPresent()) {
			server.addConnector(createHTTPS(server, httpsPort, readTimeout, certs.get()));
		}

		StreamReader streamReader = new StreamReader(getMaxRequestSize(settings));

		return new TotiServer(server, streamReader, env, charset, readTimeout, logger);
	}
	
	private ServerConnector createHTTP(Server server, int port, long timeout) {
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSendXPoweredBy(false);
		httpConfig.setSendServerVersion(false);
		// httpConfig.setSecurePort();
		httpConfig.setSecureScheme("https");
		// httpConfig.setSecurePort(securePort);
		
		HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
		HTTP2CServerConnectionFactory http2 = new HTTP2CServerConnectionFactory(httpConfig);
		
		/*ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		// The default protocol to use in case there is no negotiation.
		alpn.setDefaultProtocol(http11.getProtocol());*/

		//return new ServerConnector(server, alpn, http2, http11); // most browsers not support HTTP2 over telnet
		ServerConnector connector = new ServerConnector(server, http11, /*alpn, */http2);
		connector.setPort(port);
		connector.setIdleTimeout(timeout);
		return connector;
	}

	private ServerConnector createHTTPS(Server server, int port, long timeout, SslCredentials certs) {
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSendXPoweredBy(false);
		httpConfig.setSendServerVersion(false);
		
		SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
		sslContextFactory.setSniRequired(certs.sniHostCheck());
		if (!certs.sniHostCheck()) {
			sslContextFactory.setEndpointIdentificationAlgorithm(null);
		}
		if (certs.useTrustedClients()) {
			sslContextFactory.setTrustStorePath(certs.getTrustedClientsStore());
			sslContextFactory.setTrustStorePassword(certs.getClientTrustStorePassword());
			sslContextFactory.setTrustStoreType(certs.getClientTrustedType());
			//sslContextFactory.setTrustStore(null);
		}
		sslContextFactory.setTrustAll(certs.trustAll());
		if (certs.useCertificate()) {
			sslContextFactory.setKeyStorePath(certs.getCertificateStore());
			sslContextFactory.setKeyStorePassword(certs.getCertificateStorePassword());
			sslContextFactory.setKeyStoreType(certs.getCertificateType());
			//sslContextFactory.setKeyStore(null);
		}/* else {
			// TODO throw? or remove optional, always have cred and in this case use unsecured?
		}*/
		sslContextFactory.setCipherComparator( HTTP2Cipher.COMPARATOR );
		
		SecureRequestCustomizer secure = new SecureRequestCustomizer();
		httpConfig.addCustomizer(secure);
		secure.setSniHostCheck(certs.sniHostCheck());

		HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
		HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(httpConfig);
		
		ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		// The default protocol to use in case there is no negotiation.
		alpn.setDefaultProtocol(http11.getProtocol());
		// The ConnectionFactory for TLS.
		SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

		ServerConnector connector = new ServerConnector(server, ssl, alpn, http2, http11);
		connector.setPort(port);
		connector.setIdleTimeout(timeout);
		return connector;
	}

	/************************/
	
	private int getHttpsPort(Env env) {
		return getProperty(httpsPort, "secured-port", 443, v->v.getInteger(), env);
	}
	
	private int getHttpPort(Env env) {
		return getProperty(httpPort, "port", 80, v->v.getInteger(), env);
	}
	
	private String getCharset(Env env) {
		return getProperty(charset, "charset", "UTF-8", v->v.getString(), env);
	}

	private int getThreadPool(Env env) {
		return getProperty(threadPool, "thread-pool", 5, v->v.getInteger(), env);
	}
	
	private long getReadTimeout(Env env) {
		return getProperty(readTimeout, "read-timeout", 60000L, v->v.getLong(), env);
	}
	
	private Integer getMaxRequestSize(Env env) {
		return getProperty(maxRequestSize, "max-request-size", null, v->v.getInteger(), env);
	}
	
	private Optional<SslCredentials> getCerts(Env env) {
		if (certs != null) {
			return certs;
		}
		if (env != null) {
			SslCredentials cred = new SslCredentials();
			Env keyStore = env.getSection("key-store");
			if (!keyStore.isEmpty()) {
				cred.setCertificateStore(
					keyStore.getString("path"),
					keyStore.getString("password"),
					keyStore.getString("type")
				);
				Boolean checkSNI = keyStore.getBoolean("checkSNI");
				if (checkSNI != null) {
					cred.setSniHostCheck(checkSNI);
				}
			}
			Env trustStore = env.getSection("trust-store");
			if (!trustStore.isEmpty()) {
				cred.setTrustedClientsStore(
					trustStore.getString("path"),
					trustStore.getString("password"),
					trustStore.getString("type")
				);
			} else {
				cred.setTrustAll(true);
			}
			if (!keyStore.isEmpty() || !trustStore.isEmpty()) {
				return Optional.of(cred);
			}
		}
		return Optional.empty();
	}
	
	private <T> T getProperty(T value, String key, T defaultValue, Function<Scalar, T> get, Env env) {
		if (value != null) {
			return value;
		}
		if (env != null && env.getValue(key) != null) {
			return get.apply(env._getValue(key));
		}
		return defaultValue;
	}
	
	/****************************/

	public TotiServerFactory setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}

	public TotiServerFactory setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public TotiServerFactory setHttpsPort(int port) {
		this.httpsPort = port;
		return this;
	}

	public TotiServerFactory setHttpPort(int port) {
		this.httpPort = port;
		return this;
	}
	
	public TotiServerFactory setMaxRequestBodySize(Integer maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
		return this;
	}
	
	public TotiServerFactory setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}
	
	public TotiServerFactory setCerts(Optional<SslCredentials> certs) {
		this.certs = certs;
		return this;
	}
	
}
