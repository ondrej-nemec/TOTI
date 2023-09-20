package toti.authentication;

public class AuthentizationException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthentizationException(String message) {
		super(message);
	}

	public AuthentizationException(Throwable t) {
		super(t);
	}

	public AuthentizationException(String message, Throwable t) {
		super(message, t);
	}
	
}
