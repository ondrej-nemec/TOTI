package mvc;

import java.util.LinkedList;
import java.util.List;

public class ResponseHeaders {

	private final String nonce;
	
	private final List<String> headers;

	public ResponseHeaders(String nonce, List<String> headers) {
		this.nonce = nonce;
		this.headers = headers;
	}

	public String getNonce() {
		return nonce;
	}
	
	public List<String> getHeaders() {
		return new LinkedList<>(headers);
	}

	public List<String> getHeaders(String append) {
		List<String> list = new LinkedList<>(headers);
		list.add(append);
		return list;
	}

	public List<String> getHeaders(List<String> append) {
		List<String> list = new LinkedList<>(headers);
		list.addAll(append);
		return list;
	}
	
}
