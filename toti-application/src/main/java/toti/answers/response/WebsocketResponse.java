package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;
import toti.answers.request.Identity;

public class WebsocketResponse implements Response {
	
	private final WebSocket websocket;
	private final BiConsumer<Boolean, ByteArrayOutputStream> onMessage;
	private final Consumer<IOException> onError;
	private final Consumer<String> onClose;
	private final Headers headers;
	
	public WebsocketResponse(Headers headers,
			WebSocket websocket,
			BiConsumer<Boolean, ByteArrayOutputStream> onMessage,
			Consumer<IOException> onError,
			Consumer<String> onClose) {
		this.websocket = websocket;
		this.onError = onError;
		this.onMessage = onMessage;
		this.onClose = onClose;
		this.headers = headers;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers responseHeaders,
			Identity identity,
			ResponseContainer container,
			String charset) {
		ji.socketCommunication.http.structures.Response wbRes = new ji.socketCommunication.http.structures.Response(StatusCode.SWITCHING_PROTOCOL,protocol);
		wbRes.setHeaders(responseHeaders.getHeaders());
		wbRes.setHeaders(this.headers.getHeaders());
		websocket.accept(onMessage, onError, onClose);
		return wbRes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((onClose == null) ? 0 : onClose.hashCode());
		result = prime * result + ((onError == null) ? 0 : onError.hashCode());
		result = prime * result + ((onMessage == null) ? 0 : onMessage.hashCode());
		result = prime * result + ((websocket == null) ? 0 : websocket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WebsocketResponse other = (WebsocketResponse) obj;
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (onClose == null) {
			if (other.onClose != null) {
				return false;
			}
		} else if (!onClose.equals(other.onClose)) {
			return false;
		}
		if (onError == null) {
			if (other.onError != null) {
				return false;
			}
		} else if (!onError.equals(other.onError)) {
			return false;
		}
		if (onMessage == null) {
			if (other.onMessage != null) {
				return false;
			}
		} else if (!onMessage.equals(other.onMessage)) {
			return false;
		}
		if (websocket == null) {
			if (other.websocket != null) {
				return false;
			}
		} else if (!websocket.equals(other.websocket)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WebsocketResponse [websocket=" + websocket + ", onMessage=" + onMessage + ", onError=" + onError
				+ ", onClose=" + onClose + ", headers=" + headers + "]";
	}

}
