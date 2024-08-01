package parser;

import java.io.IOException;
import java.io.InputStream;

public abstract class ParserInputStream {

	protected final InputStream is;
	
	public ParserInputStream(final InputStream stream) {
		this.is = stream;
	}
	
	public boolean next() throws IOException {
		int b = 0;
		boolean isContinue = true;
		while(isContinue && ((b = is.read()) != -1)) {
			isContinue = parse((char)b);
		}
		if (b != -1)
			return true;
		return false;
	}
	
	abstract protected boolean parse(final char car);
}
