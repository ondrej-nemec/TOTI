package toti.answers.response;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.Headers;
import toti.security.Identity;

public class TextResponse implements Response {

	private final String text;
	private final StatusCode code;
	
	public TextResponse(StatusCode code, Object text) {
		this.text = text == null ? "" : text.toString();
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
		response.setBody(text.getBytes());
		if (!response.containsHeader("Content-Type")) {
            response.addHeader("Content-Type", "text/plain");
        }
		return response;
	}

}
