package toti.answers.response;

import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.tcpip.enums.StatusCode;

public class TextResponse implements Response {

	private final String text;
	private final StatusCode code;
	private final Headers headers;
	
	public TextResponse(StatusCode code, Headers headers, Object text) {
		this.text = text == null ? "" : text.toString();
		this.code = code;
		this.headers = headers;
	}
	
	@Override
	public FinalResponse prepare(
			Headers headers,
			Identity identity,
			ResponseContainer container,
			String charset) {
		if (!headers.containsHeader("Content-Type")) {
			headers.addHeader("Content-Type", "text/plain");
		}
		headers.setHeaders(this.headers.getHeaders());
		return new FinalResponse(code, headers, text.getBytes());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		TextResponse other = (TextResponse) obj;
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
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TextResponse [text=" + text + ", code=" + code + ", headers=" + headers + "]";
	}

}
