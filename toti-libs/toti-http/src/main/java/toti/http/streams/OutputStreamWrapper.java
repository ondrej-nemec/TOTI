package toti.http.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWrapper extends FilterOutputStream {

	private boolean isClosed = false;
	
	public OutputStreamWrapper(OutputStream out) {
		super(out);
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		this.isClosed = true;
	}

}
