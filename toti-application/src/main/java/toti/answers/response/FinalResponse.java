package toti.answers.response;

import java.nio.ByteBuffer;

import toti.answers.Headers;
import toti.http.enums.StatusCode;

public class FinalResponse {

	private final StatusCode statusCode;
	private final Headers headers;
	private final ByteBuffer body;

	public FinalResponse(StatusCode statusCode, Headers headers, String body) {
		this(statusCode, headers, body.getBytes());
	}
	
	public FinalResponse(StatusCode statusCode, Headers headers, byte[] body) {
		this(statusCode, headers, ByteBuffer.wrap(body));
	}
	
	public FinalResponse(StatusCode statusCode, Headers headers, ByteBuffer body) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
	}
	
	public FinalResponse(StatusCode statusCode, Headers headers) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = null;
	}
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
	public Headers getHeaders() {
		return headers;
	}
	public ByteBuffer getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "FinalResponse [statusCode=" + statusCode + ", headers=" + headers + ", body=" + body + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
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
		FinalResponse other = (FinalResponse) obj;
		if (body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!body.equals(other.body)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (statusCode != other.statusCode) {
			return false;
		}
		return true;
	}
	
}
