package samples.tutorial1.services;

import java.util.Arrays;

import toti.security.permissions.Permissions;
import toti.security.permissions.Rules;

public class EdgeControlPermissions implements Permissions {

	private static final long serialVersionUID = 1L;

	@Override
	public Rules getRulesForDomain(String domain) {
		return new Rules(null, Arrays.asList());
	}

}
