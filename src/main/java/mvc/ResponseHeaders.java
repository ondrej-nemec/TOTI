package mvc;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

public class ResponseHeaders {

	// private final String nonce;
	
	private final List<String> headers;

	public ResponseHeaders(/*String nonce,*/ List<String> headers) {
		// this.nonce = nonce;
		this.headers = headers;
	}

	public String getNonce() {
		return RandomStringUtils.randomAlphanumeric(50);
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
