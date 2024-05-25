package toti.http.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import ji.common.structures.ThrowingConsumer;
import toti.http.Client;
import toti.http.SslCredentials;
import toti.http.http.parsers.ExchangeFactory;
import toti.http.http.streams.InputStreamWrapper;
import toti.http.http.structures.Protocol;
import toti.http.http.structures.Request;
import toti.http.http.structures.Response;

public class HttpClient implements Client {
	
	private final String serverUrl;
	private final int port;
	private final Protocol protocol;
	
	private final int timeOut;
	private final Logger logger;
	
	// private final String charset;
	
	private final Optional<SslCredentials> ssl;
	private final ExchangeFactory factory;
	
	public HttpClient(String serverUrl, Optional<SslCredentials> ssl, Logger logger) {
		this(serverUrl, ssl.isPresent() ? 443 : 80, Protocol.HTTP_1_1, ssl, 60000, null, logger);
	}
	
	public HttpClient(
			String serverUrl, int port, Protocol protocol,
			Optional<SslCredentials> ssl, 
			int timeOut, Integer maxResponseBodySize,
			Logger logger) {
		this.serverUrl = serverUrl;
		this.logger = logger;
		this.timeOut = timeOut;
		this.ssl = ssl;
		this.port = port;
		this.protocol = protocol;
		this.factory = ExchangeFactory.create(maxResponseBodySize, logger);
	}
	
	public Response get(String uri, ThrowingConsumer<Request, Exception> createRequest) throws Exception {
		return send(HttpMethod.GET, uri, createRequest);
	}
	
	public Response post(String uri, ThrowingConsumer<Request, Exception> createRequest) throws Exception {
		return send(HttpMethod.POST, uri, createRequest);
	}
	
	public Response put(String uri, ThrowingConsumer<Request, Exception> createRequest) throws Exception {
		return send(HttpMethod.PUT, uri, createRequest);
	}
	
	public Response delete(String uri, ThrowingConsumer<Request, Exception> createRequest) throws Exception {
		return send(HttpMethod.DELETE, uri, createRequest);
	}
	
	public Response send(HttpMethod method, String uri, ThrowingConsumer<Request, Exception> createRequest) throws Exception {
		Request request = new Request(method, uri, protocol);
		createRequest.accept(request);
		Socket con = createSocket(serverUrl, port, ssl, logger);
		con.setSoTimeout(timeOut);
		return send(con, request);
	}
	
	public Response send(Request request) throws Exception {
		Socket con = createSocket(serverUrl, port, ssl, logger);
		con.setSoTimeout(timeOut);
		return send(con, request);
	}
	
	/**************/
	
	protected Response send(Socket con, Request request) throws IOException {
		try (InputStreamWrapper is = new InputStreamWrapper(new BufferedInputStream(con.getInputStream()));
			BufferedOutputStream os = new BufferedOutputStream(con.getOutputStream());) {
			factory.write(request, os);
			Response response = factory.readResponse(is);
			con.close();
			return response;
	    }
	}
}
