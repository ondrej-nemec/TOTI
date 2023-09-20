package toti.security.exceptions;

import toti.security.Action;

public class AccessDeniedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {
		super();
	}
	
	public AccessDeniedException(Object user, String destination, Action action) {
		super(user + ":" + destination + ":" + action);
	}
	
	public AccessDeniedException(Object user, String destination, Action action, Object owner) {
		super(user + ":" + destination + ":" + action + "(" + owner + ")");
	}
	
}
