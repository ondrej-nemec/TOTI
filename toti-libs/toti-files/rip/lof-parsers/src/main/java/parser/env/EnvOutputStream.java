package parser.env;

import java.io.IOException;
import java.io.OutputStream;

import exceptions.ParserSyntaxException;
import parser.ParserOutputStream;

public class EnvOutputStream extends ParserOutputStream{

	public EnvOutputStream(final OutputStream stream) {
		super(stream);
	}
	
	public void writeTwins(final String key, final String value) throws IOException {
		String result = value;
		if (key.indexOf('\n') >= 0)
			throw new ParserSyntaxException(".env", "Key could not contains new line (" + key + ")");
		if (key.indexOf('\\') >= 0)
			throw new ParserSyntaxException(".env", "Key could not contains backslash (" + key + ")");
		if (result.indexOf('\n') >= 0 || result.indexOf('"') >= 0 || result.indexOf('\'') >= 0 || result.indexOf('\\') >= 0) {

			result = result.replaceAll("\\\\", "\\\\\\\\"); // replace \ with \\
			result = result.replaceAll("\"", "\\\\\""); // replace " with \"
			result = result.replaceAll("'", "\\\\\'"); // replace ' with \'
			result = result.replaceAll("\n", "\\\\\\n"); // replace \n (one char) with \n (two chars)
			
			result = "\"" + result + "\"";
		}
		String toWrite = key + "=" + result + NEW_LINE;
		os.write(toWrite.getBytes());
	}
}
