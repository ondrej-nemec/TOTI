package samples.examples.security;

import toti.security.permissions.Permissions;
import toti.security.permissions.Rules;

public class SecurityExamplePermissions implements Permissions {
	private static final long serialVersionUID = 1L;
	
	private final String userName;
	
	public SecurityExamplePermissions(String userName) {
		this.userName = userName;
	}
	
	@Override
	public Rules getRulesForDomain(String domain) {
		// TODO Auto-generated method stub
		return null;
	}

}
