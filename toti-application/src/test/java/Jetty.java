import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class Jetty {

	// TODO https://jetty.org/docs/jetty/12/programming-guide/server/index.html
	@SuppressWarnings("CallToPrintStackTrace")
	public static void main(String[] args) {
		try {
			System.out.println("Start");
			
			
			// server thread pool
			QueuedThreadPool threadPool = new QueuedThreadPool(10);
			threadPool.setName("server");
		//	threadPool.setMaxThreads(5);
			
			Server server = new Server(threadPool);
			server.addConnector(createUnsecured(server));
		//	server.addConnector(createSecured(server));
			
		//	Connector connector = new ServerConnector(server);
		//	server.addConnector(connector);
			
			Handler handler = new HandlerImpl();
			server.setHandler(handler);
			
			server.start();
			
			server.join();
			System.out.println("End");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/*
	private static Connector createSecured(Server server) {
		// Configure the SslContextFactory with the KeyStore information.
		
	//	SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
	//	sslContextFactory.setKeyStorePath("/path/to/keystore");
	//	sslContextFactory.setKeyStorePassword("secret");

		HttpConfiguration httpsConfig = new HttpConfiguration();
		httpsConfig.addCustomizer(new SecureRequestCustomizer());
		HttpConnectionFactory https = new HttpConnectionFactory(secureConfig);
		HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(secureConfig);
		ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		alpn.setDefaultProtocol(https.getProtocol());
		ConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, https.getProtocol());
		ServerConnector connector = new ServerConnector(server, 1, 1, ssl, alpn, http2, https);
		connector.setPort(443);
		connector.setHost("127.0.0.1");
		return connector;
	}
*/
	private static Connector createUnsecured(Server server) {
		//HttpConfiguration httpsConfig = new HttpConfiguration();
		
		ServerConnector connector = new ServerConnector(
			server
			// ,new HTTP2ServerConnectionFactory(httpsConfig)
			// ,new HttpConnectionFactory(httpsConfig)
		);
		connector.setPort(80);
		//connector.setHost("127.0.0.1");
		return connector;
	}

}
