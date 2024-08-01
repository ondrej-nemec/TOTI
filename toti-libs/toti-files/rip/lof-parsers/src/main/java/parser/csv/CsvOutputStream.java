package parser.csv;

import java.io.IOException;
import java.io.OutputStream;

import parser.ParserOutputStream;

public class CsvOutputStream extends ParserOutputStream{
	
	private final char separator;
	
	private boolean isFirst = true;	
	
	public CsvOutputStream(final OutputStream stream) {
		super(stream);
		this.separator = ',';
	}
	
	public CsvOutputStream(final OutputStream stream, final char separator) {
		super(stream);
		this.separator = separator;
	}

	public void writeValue(final String value) throws IOException {
		String result = value;
		result = result.replaceAll("\"", "\"\"");
		if (result.indexOf(separator) != -1 || result.indexOf("\n") != -1)
			result = "\"" + result + "\"";
		if (!isFirst)
			result = separator + result;
		os.write(result.getBytes());
		isFirst = false;
	}
	
	public void writeNewLine() throws IOException {
		os.write(NEW_LINE.getBytes());
		isFirst = true;
	}
	
}
