package toti;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;

import ji.common.structures.ObjectBuilder;
import toti.answers.Answer;
import toti.answers.Headers;
import toti.answers.request.Request;
import toti.answers.response.FinalResponse;
import toti.http.enums.HttpMethod;
import toti.http.enums.StatusCode;
import toti.http.parsers.Form;
import toti.http.parsers.StreamReader;
import toti.http.parsers.Urlencode;
import toti.http.structures.RequestParameters;
import toti.http.structures.WebSocket;

public class MainHandler extends Handler.Abstract {
	
	//private final Logger logger;
	private final Map<String, String[]> aliases = new HashMap<>();
	private final Map<String, Answer> answers = new HashMap<>();
	
	private final Form formParser;
	private final Urlencode urlEncode;
	private final StreamReader streamReader;
	
	public MainHandler(Form formParser, Urlencode urlEncode, StreamReader streamReader, Logger logger) {
		this.formParser = formParser;
		this.urlEncode = urlEncode;
		this.streamReader = streamReader;
		//this.logger = logger;
	}

	@Override
	public boolean handle(org.eclipse.jetty.server.Request jettyRequest, org.eclipse.jetty.server.Response jettyResponse, Callback callback) throws Exception {
		Headers requestHeaders = new Headers();
		jettyRequest.getHeaders().forEach((httpField)->{
			httpField.getValueList().forEach((value)->{
				requestHeaders.addHeader(httpField.getName(), value);
			});
		});

		Object hostname = requestHeaders.getHeader("Host");
		if (hostname == null) {
			jettyResponse.setStatus(StatusCode.BAD_REQUEST.getCode());
			callback.succeeded();
			return true;
		}
		hostname = hostname.toString().split(":")[0];
		
		ObjectBuilder<WebSocket> websocket = new ObjectBuilder<>();
		if ("websocket".equals(requestHeaders.getHeader("Upgrade"))) {
			websocket.set(new WebSocket(()->{
				ServerWebSocketContainer container = ServerWebSocketContainer.get(jettyRequest.getContext());
				// This is a WebSocket upgrade request, perform a direct upgrade.
				boolean upgraded = container.upgrade(
					(rq, rs, cb)->websocket.get(),
					jettyRequest, jettyResponse, callback
				);

				if (!upgraded) {
					throw new RuntimeException("Websocket was not upgraded");
				}
			}));
			
			
		}
		
		byte[] requestBody = null;
		RequestParameters parsedBody = new RequestParameters();
		try (InputStream is = Content.Source.asInputStream(jettyRequest)) {
			Object type = requestHeaders.getHeader("Content-Type");
			Integer length = requestHeaders.getHeader("Content-Length", Integer.class);
			if (type == null || type.equals("") || type.toString().toLowerCase().startsWith("application/x-www-form-urlencoded")) {
				parsedBody = urlEncode.decode(is, length);
			} else if (type != null && type.toString().toLowerCase().startsWith("multipart/form-data")) {
				parsedBody = formParser.read(type.toString(), length, is);
			} else {
				requestBody = streamReader.readData(length, is, 0, (a)->false, false);
			}
		}
		Request request = new Request(
			jettyRequest.getHttpURI().getDecodedPath(),
			HttpMethod.valueOf(jettyRequest.getMethod().toUpperCase()),
			requestHeaders,
			urlEncode.decode(jettyRequest.getHttpURI().getQuery()),
			parsedBody,
			requestBody,
			websocket.isPresent() ? Optional.of(websocket.get()) : Optional.empty()
		);
		String ip = org.eclipse.jetty.server.Request.getRemoteAddr(jettyRequest);

		Answer answer = answers.get(hostname);
		FinalResponse response = answer.accept(request, ip);
		
		if (websocket.isPresent() && websocket.get().isAccepted()) {
			return true;
		}
		
		jettyResponse.setStatus(response.getStatusCode().getCode());
		response.getHeaders().forEach((name, values)->{
			values.forEach((value)->{
				jettyResponse.getHeaders().add(name, value.toString());
			});
		});

		ByteBuffer responseBody = response.getBody();
		if (responseBody != null) {
			jettyResponse.write(true, responseBody, callback);
		}
		
		callback.succeeded();
		return true;
	}
	
	public void addApplication(Answer answer, String hostname, String...alias) {
		answers.put(hostname, answer);
		aliases.put(hostname, alias);
	}
	
	public void removeApplication(String hostname) {
		answers.remove(hostname);
		if (aliases.get(hostname) != null) {
			for (String alias : aliases.get(hostname)) {
				answers.remove(alias);
			}
		}
	}
}
