package parser;

import java.io.File;
import java.io.OutputStream;

public abstract class ParserOutputStream {

	protected final String NEW_LINE = File.separator == "/" ? "\n" : "\r\n";
	
	protected final OutputStream os;
	
	public ParserOutputStream(final OutputStream stream) {
		this.os = stream;
	}
}
