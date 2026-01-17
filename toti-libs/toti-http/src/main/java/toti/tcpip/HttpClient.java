package toti.tcpip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import toti.lib.common.structures.ThrowingConsumer;
import toti.tcpip.client.ExchangeFactory;
import toti.tcpip.client.ExchangeRequest;
import toti.tcpip.client.ExchangeResponse;
import toti.tcpip.client.Protocol;
import toti.tcpip.enums.HttpMethod;
import toti.tcpip.streams.InputStreamWrapper;

public class HttpClient {
	
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
	
	public ExchangeResponse get(String uri, ThrowingConsumer<ExchangeRequest, Exception> createRequest) throws Exception {
		return send(HttpMethod.GET, uri, createRequest);
	}
	
	public ExchangeResponse post(String uri, ThrowingConsumer<ExchangeRequest, Exception> createRequest) throws Exception {
		return send(HttpMethod.POST, uri, createRequest);
	}
	
	public ExchangeResponse put(String uri, ThrowingConsumer<ExchangeRequest, Exception> createRequest) throws Exception {
		return send(HttpMethod.PUT, uri, createRequest);
	}
	
	public ExchangeResponse delete(String uri, ThrowingConsumer<ExchangeRequest, Exception> createRequest) throws Exception {
		return send(HttpMethod.DELETE, uri, createRequest);
	}
	
	public ExchangeResponse send(HttpMethod method, String uri, ThrowingConsumer<ExchangeRequest, Exception> createRequest) throws Exception {
		ExchangeRequest request = new ExchangeRequest(method, uri, protocol);
		createRequest.accept(request);
		Socket con = createSocket(serverUrl, port, ssl, logger);
		con.setSoTimeout(timeOut);
		return send(con, request);
	}
	
	public ExchangeResponse send(ExchangeRequest request) throws Exception {
		Socket con = createSocket(serverUrl, port, ssl, logger);
		con.setSoTimeout(timeOut);
		return send(con, request);
	}
	
	/**************/
	
	protected ExchangeResponse send(Socket con, ExchangeRequest request) throws IOException {
		try (InputStreamWrapper is = new InputStreamWrapper(new BufferedInputStream(con.getInputStream()));
			BufferedOutputStream os = new BufferedOutputStream(con.getOutputStream());) {
			factory.write(request, os);
			ExchangeResponse response = factory.readResponse(is);
			con.close();
			return response;
		}
	}
	
	private Socket createSocket(String ip, int port, Optional<SslCredentials> ssl, Logger logger) throws Exception {
		if (ssl.isPresent()) {
		return SSL.getSSLContext(ssl.get()).getSocketFactory().createSocket(ip, port);
		} else {
			logger.warn("No secured credentials, connection is not secured");
			return new Socket(ip, port);
		}
	}
	
}
