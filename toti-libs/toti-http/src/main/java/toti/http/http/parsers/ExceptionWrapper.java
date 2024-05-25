package toti.http.http.parsers;

import java.io.IOException;

public class ExceptionWrapper extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ExceptionWrapper(IOException e) {
		super(e);
	}
	@Override
	public synchronized IOException getCause() {
		return (IOException)super.getCause();
	}

}
