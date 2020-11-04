package mvc;

import java.util.LinkedList;
import java.util.List;

public class ResponseHeaders {
	
	private final List<String> headers;
	
	private final boolean isGeneral;

	public ResponseHeaders(List<String> headers) {
		this(headers, true);
	}
	
	private ResponseHeaders(List<String> headers, boolean isGeneral) {
		this.headers = headers;
		this.isGeneral = isGeneral;
	}
	
	public ResponseHeaders get() {
		return new ResponseHeaders(new LinkedList<>(headers), false);
	}
	
	public void addHeader(String header) {
		if (isGeneral) {
			throw new RuntimeException("You cannot set header yet");
		}
		this.headers.add(header);
	}
	
	public void addHeaders(List<String> headers) {
		if (isGeneral) {
			throw new RuntimeException("You cannot set header yet");
		}
		this.headers.addAll(headers);
	}
	
	public List<String> getHeaders() {
		return headers;
	}
	
/*	
	public ResponseHeaders withHeaders(List<String> headers) {
		return new ResponseHeaders(getHeaders(headers));
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
*/	
}
