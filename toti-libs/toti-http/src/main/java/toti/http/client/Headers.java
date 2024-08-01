package toti.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import toti.http.parsers.StreamReader;

public class Headers {
	
	private final StreamReader stream;
	
	public Headers(StreamReader stream) {
		this.stream = stream;
	}

	public void write(Exchange exchange, OutputStream bos) throws IOException {
		for (Entry<String, List<Object>> header : exchange.getHeaders().entrySet()) {
			for (Object headerValue : header.getValue()) {
			//	bos.write('\n'); // end of previous header or first line
				bos.write(String.format("%s: %s", header.getKey(), headerValue).getBytes());
				bos.write(stream.getNewLine()); // end of header line
			}
        }
		bos.write(stream.getNewLine()); // separator
		bos.flush();
	}
	
	public List<String> read(Exchange exchange, InputStream bis) throws IOException {
		List<String> errors = new LinkedList<>();
		String line = stream.readLine(bis);
        while (line != null && !line.isEmpty()) {
    		String[] property = line.split(": ", 2);
        	if (property.length == 2 && ! property[0].isEmpty()) {
        		exchange.addHeader(property[0], property[1]);
        	} else if (property.length == 1){
        		exchange.addHeader(property[0], "");
        	} else {
        		errors.add("Invalid header line " + line);
        	}
        	line = stream.readLine(bis);
        }
        return errors;
	}
}
