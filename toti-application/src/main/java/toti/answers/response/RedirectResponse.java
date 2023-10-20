package toti.answers.response;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.answers.router.Link;

public class RedirectResponse implements Response {

	private final String url;
	private final StatusCode code;
	private final Headers headers;
	
	public RedirectResponse(StatusCode code, Headers headers, String url, boolean allowOutOfAppRedirect) {
		if (!allowOutOfAppRedirect && !Link.isRelative(url)) {
			throw new RuntimeException("Open redirection is not allowed");
		}
		this.url = url;
		this.code = code;
		this.headers = headers;
	}
	
	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers header,
			Identity identity,
			ResponseContainer container,
			String charset) {
		header.addHeader("Location", url);
		ji.socketCommunication.http.structures.Response res = new ji.socketCommunication.http.structures.Response(code, protocol);
		res.setHeaders(header.getHeaders());
		res.setHeaders(this.headers.getHeaders());
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		RedirectResponse other = (RedirectResponse) obj;
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
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RedirectResponse [url=" + url + ", code=" + code + ", headers=" + headers + "]";
	}

}
