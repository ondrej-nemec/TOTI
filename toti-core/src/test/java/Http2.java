/*import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.HeadersFrame;
*/
public class Http2 /*implements ServerSessionListener*/ {
	
	/*
	// Create a ServerConnector with RawHTTP2ServerConnectionFactory.
	RawHTTP2ServerConnectionFactory http2 = new RawHTTP2ServerConnectionFactory(new Http2());
	// Configure the max number of concurrent requests.
	http2.setMaxConcurrentStreams(128);
	// Enable support for CONNECT.
	http2.setConnectProtocolEnabled(true);

	// Create the ServerConnector.
	ServerConnector connector = new ServerConnector(server, http2);
	// Add the Connector to the Server
	server.addConnector(connector);
	//*/
	
	/*
	@Override
	public void onAccept(Session session) {
		SocketAddress remoteAddress = session.getRemoteSocketAddress();
		System.out.println("ACCEPT HTTP 2 " + remoteAddress);
	}
	@Override
	public Map<Integer, Integer> onPreface(Session session) {
		// Customize the settings, for example:
		Map<Integer, Integer> settings = new HashMap<>();

		// Tell the client that HTTP/2 push is disabled.
		// settings.put(SettingsFrame.ENABLE_PUSH, 0);

		return settings;
	}
	@Override
	public Stream.Listener onNewStream(Stream stream, HeadersFrame frame)  {
		// This is the "new stream" event, so it's guaranteed to be a request.
		MetaData.Request request = (MetaData.Request)frame.getMetaData();
		System.out.println("HTTP 2");
		System.out.println("  Method: " + request.getMethod());
		System.out.println("  URI 1: " + request.getHttpURI().getDecodedPath());
		System.out.println("  URI 2: " + request.getHttpURI().getPath()); // plain url
		System.out.println("  URI with query: " + request.getHttpURI().getPathQuery()); // full url
		System.out.println("  Query: " + request.getHttpURI().getQuery()); // null pokud nic
		System.out.println("  Headers: " + request.getHttpFields());
		System.out.println("  " );
		
		stream.demand();
		// Return a Stream.Listener to handle the request events,
		// for example request content events or a request reset.
		return new Stream.Listener() {
			@Override
			public void onDataAvailable(Stream stream) {
				Stream.Data data = stream.readData();

				if (data == null)
				{
					stream.demand();
					return;
				}

				// Get the content buffer.
				ByteBuffer buffer = data.frame().getByteBuffer();

				// Consume the buffer, here - as an example - just log it.
				System.out.println("Consuming buffer " + buffer);

				// Tell the implementation that the buffer has been consumed.
				data.release();

				if (!data.frame().isEndStream()) {
					// Demand more DATA frames when they are available.
					stream.demand();
				}
			}
		};
	}
	*/
}
