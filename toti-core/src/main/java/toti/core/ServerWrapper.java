package toti.core;

import java.time.Duration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;

public class ServerWrapper {
	private final Server server;
	
	public ServerWrapper(Server server) {
		this.server = server;
	}
	
	public void init(MainHandler handler, long timeout) {
		// required for websockets
		ContextHandler contextHandler = new ContextHandler();
		ServerWebSocketContainer container = ServerWebSocketContainer.ensure(server, contextHandler);
		container.setIdleTimeout(Duration.ofMillis(timeout));
		contextHandler.setHandler(handler);
		
		server.setHandler(contextHandler);
	}
	
	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}
}
