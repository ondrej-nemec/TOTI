package toti.http.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import toti.http.Servant;
import toti.http.Server;
import toti.http.SslCredentials;
import toti.http.http.parsers.ExchangeFactory;
import toti.http.http.profiler.HttpServerProfilerEvent;
import toti.http.http.streams.InputStreamWrapper;
import toti.http.http.streams.OutputStreamWrapper;
import toti.http.http.structures.Protocol;
import toti.http.http.structures.Request;
import toti.http.http.structures.Response;
import toti.http.http.structures.WebSocket;

public class HttpServer implements Servant {
	
	// public static HttpServerProfiler PROFILER = null;
		
	private final Logger logger;
	private final Map<String, ResponseFactory> applications;
	
	private final ExchangeFactory factory;
	
	public HttpServer(
			Integer maxRequestBodySize,
			Logger logger) {
		this.logger = logger;
		this.applications = new HashMap<>();
		this.factory = ExchangeFactory.create(maxRequestBodySize, logger);
	}
	
	@Override
	public void serve(Socket socket, String charset) throws IOException {
		try (InputStreamWrapper is = new InputStreamWrapper(new BufferedInputStream(socket.getInputStream()));
				OutputStreamWrapper os = new OutputStreamWrapper(new BufferedOutputStream(socket.getOutputStream()));) {
			serve(is, os, socket.getInetAddress().toString());
       } catch (Exception e) {
    	   logger.fatal("Uncaught exception. Client: " + socket.getInetAddress().toString(), e);
       }
	}
    
    public HttpServer addApplication(ResponseFactory servant, String hostname, String... aliases) {
    	applications.put(hostname, servant);
    	for (String alias : aliases) {
    		applications.put(alias, servant);
    	}
    	return this;
    }
    
    public void removeApplication(String hostname) {
    	applications.remove(hostname);
    }
    
    public Server createWebServer(int port,
    		int threadPool,
    		long readTimeout,
    		Optional<SslCredentials> ssl,
    		String charset) throws Exception {
    	return new Server(
    			port, 
    			threadPool,
    			readTimeout,
    			this,
    			ssl,
    			charset,
    			logger
    	);
    }
	
	protected void serve(InputStreamWrapper is, OutputStreamWrapper os, String clientIp) throws IOException {
		//profile(HttpServerProfilerEvent.REQUEST_ACCEPT);
		Map<HttpServerProfilerEvent, Long> events = new HashMap<>();
		events.put(HttpServerProfilerEvent.REQUEST_ACCEPT, System.currentTimeMillis());
		
		Request request = factory.readRequest(is);
		// profile(HttpServerProfilerEvent.REQUEST_PARSED);
		events.put(HttpServerProfilerEvent.REQUEST_PARSED, System.currentTimeMillis());
		if (request == null) {
			Response response = new Response(StatusCode.OK, Protocol.HTTP_1_1);
			response.setBody(Protocol.getAll().getBytes());
			factory.write(response, os);
			/*logger.error("Unparsed request");
			factory.write(new Response(StatusCode.BAD_REQUEST, Protocol.HTTP_1_1), os);*/
			return;
		}
		//profile(HttpServerProfilerEvent.RESPONSE_CREATED);
		
		Optional<WebSocket> websocket = Optional.empty();
		if ("websocket".equals(request.getHeader("Upgrade"))) {
			websocket = Optional.of(new WebSocket(os, is, request));
		}
		
		Object hostname = request.getHeader("Host");
		if (hostname != null) {
			String host = hostname.toString().split(":")[0];
			if (applications.containsKey(host)) {
				ResponseFactory resFactory = applications.get(host);
				Response response = resFactory.accept(request, clientIp, websocket);
				events.put(HttpServerProfilerEvent.RESPONSE_CREATED, System.currentTimeMillis());
				if (websocket.isPresent() && websocket.get().isAccepted()) {
					response.setBodyWebsocket(websocket.get());
				}
				factory.write(response, os);
				events.put(HttpServerProfilerEvent.RESPONSE_SENDED, System.currentTimeMillis());
				if (resFactory.getProfiler() != null) {
					resFactory.getProfiler().log(events);
				}
			} else {
				logger.warn("Request on not existing hostname: '" + host + "', IP: " + clientIp);
				factory.write(new Response(StatusCode.BAD_REQUEST, request.getProtocol()), os);
			}
		} else {
			logger.warn("Request Host header not specified");
			factory.write(new Response(StatusCode.BAD_REQUEST, request.getProtocol()), os);
			return;
		}
		/*Response response = createResponce.accept(request, clientIp, websocket);
		if (websocket.isPresent() && websocket.get().isAccepted()) {
			response.setBodyWebsocket(websocket.get());
		}
		factory.write(response, os);*/
		// profile(HttpServerProfilerEvent.RESPONSE_SENDED);
	}
	/*
	private void profile(HttpServerProfilerEvent event) {
		if (PROFILER != null) {
			PROFILER.log(event);
		}
	}
	*/
}
