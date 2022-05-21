package toti.response;

import java.io.IOException;
import java.util.function.Consumer;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import ji.translator.Translator;
import toti.Headers;
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
	public ji.socketCommunication.http.structures.Response getResponse(
			String protocol, Headers responseHeaders, TemplateFactory templateFactory, Translator translator,
			Authorizator authorizator, Identity identity, MappedUrl current, String charset) {
		ji.socketCommunication.http.structures.Response wbRes = new ji.socketCommunication.http.structures.Response(StatusCode.SWITCHING_PROTOCOL,protocol);
		wbRes.setHeaders(responseHeaders.getHeaders());
		websocket.accept(onMessage, onError);
		return wbRes;
	}

}
