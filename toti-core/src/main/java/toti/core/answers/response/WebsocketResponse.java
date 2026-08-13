package toti.core.answers.response;

import toti.core.answers.Headers;
import toti.core.answers.action.WebSocket;
import toti.lib.common.exceptions.LogicException;
import toti.lib.tcpip.enums.StatusCode;

public class WebsocketResponse implements Response {
	
	private final Headers headers;
	
	public WebsocketResponse(WebSocket webSocket, Headers headers) {
		this.headers = headers;
		if (!webSocket.isAccepted()) {
			throw new LogicException("You cannot return WebSocketResponse if WebScket was not accepted.");
		}
	}

	@Override
	public FinalResponse prepare(ResponseContainer container) {
		container.headers().setHeaders(this.headers.getHeaders());
		return new FinalResponse(StatusCode.SWITCHING_PROTOCOL, container.headers());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
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
		return true;
	}

	@Override
	public String toString() {
		return "WebsocketResponse [headers=" + headers + "]";
	}

}
