package toti.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EventListener;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;

public class MainHandler implements Handler {
	
	private Server server;
	private final Logger logger;
	
	public MainHandler(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean handle(Request request, Response response, Callback callback) throws Exception {
		/*new Handler.Abstract() {

			@Override
			public boolean handle(Request request, Response response, Callback callback) throws Exception {
				// TODO Auto-generated method stub
				return false;
			}
			
		};*/

		System.out.println("Request");
		System.out.println("  Method: " + request.getMethod()); // parada
		System.out.println("  URI 1: " + request.getHttpURI().getDecodedPath());
		/*System.out.println("  URI 2: " + request.getHttpURI().getPath()); // plain url
		System.out.println("  URI with query: " + request.getHttpURI().getPathQuery()); // full url
		System.out.println("  Query: " + request.getHttpURI().getQuery()); // null pokud nic
		System.out.println("  Headers: " + request.getHeaders()); // parada
		System.out.println("  " );
		
		
		System.out.println();
		System.out.println();*/
		
		// TODO ip, body a websocket
		
		response.setStatus(200);
		response.getHeaders().add("Test", Arrays.asList("a", "b"));
		// Response.sendRedirect(request, response, callback, null);
		// Response.writeError(request, response, callback, 0);
		//*
		response.write(true, ByteBuffer.wrap("Working".getBytes("utf-8")), callback);
		/*/
		// vice volani za sebou nefunguje
		response.write(false, ByteBuffer.wrap("Working".getBytes("utf-8")), callback);
		response.write(true, ByteBuffer.wrap("<br>HERE".getBytes("utf-8")), callback);
		response.write(false, ByteBuffer.wrap("<br>And more".getBytes("utf-8")), callback);
		//*/
		
		return true; // nevim, co to dela
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public boolean addEventListener(EventListener listener) {
		// TODO Auto-generated method stub
		System.err.println("---> Add listener");
		return false;
	}

	@Override
	public boolean removeEventListener(EventListener listener) {
		// TODO Auto-generated method stub
		System.err.println("---> Remove listener");
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.err.println("---> Destroy");
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setServer(Server server) {
		this.server = server;
	}

}
