package toti.files.json;

import java.io.IOException;

public class JsonStreamException extends IOException {

	private static final long serialVersionUID = 1L;

	public JsonStreamException(Throwable t) {
		super(t);
	}
	
	public JsonStreamException(String message) {
		super(message);
	}
	
}
