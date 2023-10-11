package toti.answers.response;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.Headers;
import toti.security.Identity;

public class EmptyResponse  implements Response {

	private final StatusCode code;
	
	public EmptyResponse(StatusCode code) {
		this.code = code;
	}
	
	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers header,
			Identity identity,
			ResponseContainer container,
			String charset) {
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		response.setHeaders(header.getHeaders());
		return response;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		return true;
	}

	@Override
	public String toString() {
		return "EmptyResponse [code=" + code + "]";
	}

}
