package toti.answers.response;

import ji.common.exceptions.LogicException;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.http.enums.StatusCode;
import toti.http.structures.WebSocket;

public class WebsocketResponse implements Response {
	
	private final Headers headers;
	
	public WebsocketResponse(WebSocket webSocket, Headers headers) {
		this.headers = headers;
		if (!webSocket.isAccepted()) {
			throw new LogicException("You cannot return WebSocketResponse if WebScket was not accepted.");
		}
	}

	@Override
	public FinalResponse prepare(
			Headers responseHeaders,
			Identity identity,
			ResponseContainer container,
			String charset) {
		responseHeaders.setHeaders(this.headers.getHeaders());
		return new FinalResponse(StatusCode.SWITCHING_PROTOCOL, responseHeaders);
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
