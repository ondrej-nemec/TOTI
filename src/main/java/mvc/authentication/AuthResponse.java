package mvc.authentication;

import java.util.List;

public class AuthResponse {

	private final String content;
	private final List<String> headers;
	
	public AuthResponse(String content, List<String> headers) {
		this.content = content;
		this.headers = headers;
	}

	public String getContent() {
		return content;
	}

	public List<String> getHeaders() {
		return headers;
	}
	
}
