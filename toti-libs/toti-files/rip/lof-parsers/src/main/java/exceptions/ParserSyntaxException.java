package exceptions;

public class ParserSyntaxException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ParserSyntaxException() {}
	
	public ParserSyntaxException(final String message) {
		super(message);
	}	
	
	public ParserSyntaxException(final String format, final String message) {
		super("Parser: " + format + ", " + message);
	}

}
