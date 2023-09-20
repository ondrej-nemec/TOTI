package toti.security.exceptions;

import toti.security.Action;

public class NotAllowedActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAllowedActionException(Action action) {
		super("Action: " + action + " is not allowed as rule.");
	}

	public NotAllowedActionException(String message) {
		super(message);
	}
	
}
