package toti.response;

import java.io.IOException;
import java.util.function.Consumer;

import ji.socketCommunication.http.server.RestApiResponse;
import ji.socketCommunication.http.server.WebSocket;
import ji.translator.Translator;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;

public class WebsocketResponse implements Response {
	
	private final WebSocket websocket;
	private final Consumer<String> onMessage;
	private final Consumer<IOException> onError;
	
	public WebsocketResponse(WebSocket websocket, Consumer<String> onMessage, Consumer<IOException> onError) {
		this.websocket = websocket;
		this.onError = onError;
		this.onMessage = onMessage;
	}

	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator,
			Authorizator authorizator, Identity identity, MappedUrl current, String charset) {
		return RestApiResponse.webSocketResponse(header.getHeaders(), websocket, onMessage, onError);
	}

	@Override
	public void addParam(String name, Object value) {}

}
