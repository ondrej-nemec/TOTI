package toti.security;

public class Authorizator {

	public boolean isAllowed(User user, String domain, Action action) {
		try {
			authorize(user, domain, action);
			return true;
		} catch(AccessDeniedException e) {
			return false;
		}
	}
	
	public void authorize(User user, String domain, Action action) {
		// TODO
		/*
		 * get where user id a domain
		 */
	}

	public void authorize(User user, String domain, Action action, Object ownerId) {
		// TODO
	}
	
}
