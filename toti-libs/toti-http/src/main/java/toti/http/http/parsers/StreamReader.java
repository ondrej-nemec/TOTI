package toti.http.http.parsers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

public class StreamReader {
	
	private final Integer maxBodySize;
	
	public StreamReader(Integer maxBodySize) {
		this.maxBodySize = maxBodySize;
	}
	
	public byte[] getNewLine() {
		return "\r\n".getBytes();
	}

	public void write(byte[] bytes, OutputStream os) throws IOException {
		os.write(bytes);
	}
	
	public void write(int b, OutputStream os) throws IOException {
		os.write(b);
	}
	
	public String readLine(InputStream bis) throws IOException {
		byte[] data = readData(null, bis, 0, (actual)->{
			return actual == '\n';
		}, true);
		if (data.length == 0) {
			return null;
		}
		return new String(data);
	}

	public byte[] readData(Integer length, InputStream bis, 
			int readedBytes, Function<Integer, Boolean> close, boolean ignoreNewLine) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		if (length == null) {
			/*
			int value = bis.read(); // block until some data available or timeout
			while (value > -1) {
    			if (maxBodySize != null && bytes.size() + readedBytes > maxBodySize) {
	        		throw new IOException("Maximal body size overflow. Max size (b) " + maxBodySize);
	        	}
	        	if ( ! (ignoreNewLine && (value == '\n' || value == '\r'))) {
	        		 bytes.write(value);
	        	}
	            if (bis.available() == 0 || close.apply(value)) {
	               return bytes.toByteArray();
	            } else {
	            	value = bis.read();
	            }
    		}
			/*/
			if (bis.available() > 0) { // always 0 before read start
		        int value;
		        while((value = bis.read()) != -1) {
		        	if (maxBodySize != null && bytes.size() + readedBytes > maxBodySize) {
		        		throw new IOException("Maximal body size overflow. Max size (b) " + maxBodySize);
		        	}
		        	if ( ! (ignoreNewLine && (value == '\n' || value == '\r'))) {
		        		 bytes.write(value);
		        	}
		            if (bis.available() == 0 || close.apply(value)) {
		               return bytes.toByteArray();
		            }
		        }
		    }
			//*/
		} else {
			int readed = readedBytes;
			while (readed < length) {
				if (maxBodySize != null && readed > maxBodySize) {
					throw new IOException("Maximal body size overflow. Max size (b) " + maxBodySize);
				}
				int value = bis.read();
				if ( ! (ignoreNewLine && (value == '\n' || value == '\r'))) {
	        		 bytes.write(value);
	        		 readed = bytes.size() + readedBytes;
	        	}
				if (bis.available() == 0 && length != readed && value != -1) {
					// TODO porad zlobi, co s tim
					System.err.println("Content length not match expecation. Expected: " + length + ", actual: " + (readed + readedBytes));
	            	//throw new IOException("Content length not match expecation. Expected: " + length + ", actual: " + (readed + readedBytes));
	            }
	            if (close.apply(value)) {
	            	return bytes.toByteArray();
	            }
			}
		}
		return bytes.toByteArray();
	}
}
