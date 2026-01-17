package toti;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;

import toti.answers.Answer;
import toti.answers.Headers;
import toti.answers.request.Request;
import toti.answers.response.FinalResponse;
import toti.common.structures.ObjectBuilder;
import toti.hosts.AnswerWrapper;
import toti.hosts.Hosts;
import toti.http.enums.HttpMethod;
import toti.http.parsers.Form;
import toti.http.parsers.StreamReader;
import toti.http.parsers.Urlencode;
import toti.http.structures.RequestParameters;
import toti.http.structures.WebSocket;

public class MainHandler extends Handler.Abstract {

	private final Logger logger;
	private final Hosts answers = new Hosts();
	
	private final Form formParser;
	private final Urlencode urlEncode;
	private final StreamReader streamReader;
	
	public MainHandler(Form formParser, Urlencode urlEncode, StreamReader streamReader, Logger logger) {
		this.formParser = formParser;
		this.urlEncode = urlEncode;
		this.streamReader = streamReader;
		this.logger = logger;
	}

	@Override
	public boolean handle(org.eclipse.jetty.server.Request jettyRequest, org.eclipse.jetty.server.Response jettyResponse, Callback callback) throws Exception {
		Headers requestHeaders = new Headers();
		jettyRequest.getHeaders().forEach((httpField)->{
			httpField.getValueList().forEach((value)->{
				requestHeaders.addHeader(httpField.getName(), value);
			});
		});
		
		String uri = jettyRequest.getHttpURI().getDecodedPath();
		Object hostname = requestHeaders.getHeader("Host");
		String applicationName = hostname.toString().split(":")[0];
		String path = "";
		int index = uri.indexOf("/", 1);
		if (index != -1) {
			path = uri.substring(1, index);
		}

		AnswerWrapper selected = answers.get(applicationName, path);
		// Answer answer = answers.get(applicationName);
		if (!selected.isUsed()) {
			logger.warn("Request to unknown application: " + applicationName);
			// TODO some pretty error message?
			jettyResponse.setStatus(404);
			callback.succeeded();
			return true;
		}
		if (selected.usePath()) {
			uri = uri.substring(index);
		}
		Answer answer = selected.getAnswer();
		
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
			uri,
			HttpMethod.valueOf(jettyRequest.getMethod().toUpperCase()),
			requestHeaders,
			urlEncode.decode(jettyRequest.getHttpURI().getQuery()),
			parsedBody,
			requestBody,
			websocket.isPresent() ? Optional.of(websocket.get()) : Optional.empty()
		);
		String ip = org.eclipse.jetty.server.Request.getRemoteAddr(jettyRequest).substring(1);
		ip = ip.substring(0, ip.length()-1);

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
			jettyResponse.getHeaders().put("Content-Length", response.getLength());
			jettyResponse.write(true, responseBody, new Callback() {
				@Override
				public void succeeded() {
					//jettyResponse.complete();   // *** DŮLEŽITÉ ***
					callback.succeeded();
				}

				@Override
				public void failed(Throwable x) {
					callback.failed(x);
				}
			});
		} else {
			callback.succeeded();
		}
		
		callback.succeeded();
		return true;
	}

	public void addApplication(Answer answer, List<String> hostnames, List<String> paths) {
		answers.add(answer, hostnames, paths);
	}
	
	public void removeApplication(List<String> hostnames, List<String> paths) {
		answers.remove(hostnames, paths);
	}
}
