package toti.security;

import acl.Action;
import acl.structures.AclDestination;
import acl.structures.AclUser;

public class AccessDeniedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {
		super();
	}
	
	public AccessDeniedException(AclUser user, AclDestination destination, Action action) {
		super(user + ":" + destination + ":" + action);
	}
	
}
