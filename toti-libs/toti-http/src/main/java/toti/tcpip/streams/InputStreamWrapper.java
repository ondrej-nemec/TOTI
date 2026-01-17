package toti.tcpip.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends FilterInputStream {
	
	private boolean isClosed = false;
	private boolean isReaded = false;

	public InputStreamWrapper(InputStream is) {
		super(is);
	}
	
	@Override
	public int read() throws IOException {
		isReaded = true;
		if (isClosed) {
			return -1;
		}
		return super.read();
	}
	
	@Override
	public int available() throws IOException {
		if (!isReaded) {
			return 1;
		}
		return super.available();
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
