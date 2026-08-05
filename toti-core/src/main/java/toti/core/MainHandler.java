package toti.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;

import toti.core.answers.Answer;
import toti.core.answers.ExceptionAnswer;
import toti.core.answers.Headers;
import toti.core.answers.request.Identity;
import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.core.application.register.Register;
import toti.core.hosts.AnswerWrapper;
import toti.core.hosts.Hosts;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.parsers.Form;
import toti.lib.tcpip.parsers.StreamReader;
import toti.lib.tcpip.parsers.Urlencode;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.tcpip.structures.WebSocket;

public class MainHandler extends Handler.Abstract {

	private final Logger logger;
	private final Hosts answers = new Hosts();
	
	private final Form formParser;
	private final Urlencode urlEncode;
	private final StreamReader streamReader;
	private final AnswerWrapper defaultAnswer;
	
	public MainHandler(Form formParser, Urlencode urlEncode, StreamReader streamReader, Logger logger) {
		this.formParser = formParser;
		this.urlEncode = urlEncode;
		this.streamReader = streamReader;
		this.logger = logger;
		this.defaultAnswer = defaultAnswer(logger);
	}

	private AnswerWrapper defaultAnswer(Logger logger) {
		return new AnswerWrapper(new Answer(null, null, null, null, null, null, null) {
			private final ExceptionAnswer def = new ExceptionAnswer(
				new Register(null, null, null, null), x->false, null, logger
			);
			@Override
			public FinalResponse accept(Request request, String ipAddress) throws IOException {
				return def.answer(request, StatusCode.NOT_FOUND, null, new Identity(ipAddress, null, new HashMap<>(), Optional.empty(), null) {
					
				}, null, new Headers(), "utf-8");
			}
		
		}, false);
	}

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public boolean handle(org.eclipse.jetty.server.Request jettyRequest, org.eclipse.jetty.server.Response jettyResponse, Callback callback) throws Exception {
		try {
			return _handle(jettyRequest, jettyResponse, callback);
		} catch (Throwable t) {
			logger.fatal("Cannot server request", t);
			jettyResponse.setStatus(500);
			callback.succeeded();
			return true;
		}
	}

	private boolean _handle(org.eclipse.jetty.server.Request jettyRequest, org.eclipse.jetty.server.Response jettyResponse, Callback callback) throws Exception {
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
			selected = defaultAnswer;
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
			} else if (type.toString().toLowerCase().startsWith("multipart/form-data")) {
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

	public void addApplication(Answer answer, String hostname, String path) {
		answers.add(answer, hostname, path);
	}
	
	public void removeApplication(String hostname, String path) {
		answers.remove(hostname, path);
	}
}
