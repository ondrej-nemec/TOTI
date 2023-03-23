package toti.response;

import ji.socketCommunication.http.StatusCode;
import toti.Headers;
import toti.security.Identity;
import toti.url.Link;

public class RedirectResponse implements Response {

	private final String url;
	private final StatusCode code;
	
	public RedirectResponse(StatusCode code, String url, boolean allowOutOfAppRedirect) {
		if (!allowOutOfAppRedirect && !Link.isRelative(url)) {
			throw new RuntimeException("Open redirection is not allowed");
		}
		this.url = url;
		this.code = code;
	}
	
	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			String protocol,
			Headers header,
			Identity identity,
			ResponseContainer container,
			String charset) {
		header.addHeader("Location", url);
		ji.socketCommunication.http.structures.Response res = new ji.socketCommunication.http.structures.Response(code, protocol);
		res.setHeaders(header.getHeaders());
		return res;
	}

}
