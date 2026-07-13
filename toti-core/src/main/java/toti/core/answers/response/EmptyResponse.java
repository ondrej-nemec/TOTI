package toti.core.answers.response;

import toti.core.answers.Headers;
import toti.lib.tcpip.enums.StatusCode;

public class EmptyResponse implements Response {

	private final StatusCode code;
	private final Headers headers;
	
	public EmptyResponse(StatusCode code, Headers headers) {
		this.code = code;
		this.headers = headers;
	}
	
	@Override
	public FinalResponse prepare(ResponseContainer container) {
		container.headers().setHeaders(this.headers.getHeaders());
		return new FinalResponse(code, container.headers());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		EmptyResponse other = (EmptyResponse) obj;
		if (code != other.code) {
			return false;
		}
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
		return "EmptyResponse [code=" + code + ", headers=" + headers + "]";
	}

}
