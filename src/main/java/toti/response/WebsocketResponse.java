package toti.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;
import toti.security.Identity;

public class WebsocketResponse implements Response {
	
	private final WebSocket websocket;
	private final BiConsumer<Boolean, ByteArrayOutputStream> onMessage;
	private final Consumer<IOException> onError;
	private final Consumer<String> onClose;
	
	public WebsocketResponse(
			WebSocket websocket,
			BiConsumer<Boolean, ByteArrayOutputStream> onMessage,
			Consumer<IOException> onError,
			Consumer<String> onClose) {
		this.websocket = websocket;
		this.onError = onError;
		this.onMessage = onMessage;
		this.onClose = onClose;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			String protocol,
			Headers responseHeaders,
			Identity identity,
			ResponseContainer container,
			String charset) {
		ji.socketCommunication.http.structures.Response wbRes = new ji.socketCommunication.http.structures.Response(StatusCode.SWITCHING_PROTOCOL,protocol);
		wbRes.setHeaders(responseHeaders.getHeaders());
		websocket.accept(onMessage, onError, onClose);
		return wbRes;
	}

}
