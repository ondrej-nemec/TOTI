package toti.samples.form;

import toti.security.permissions.Permissions;
import toti.security.permissions.Rules;

public class FormPermissions implements Permissions {

	private static final long serialVersionUID = 1L;

	@Override
	public Rules getRulesForDomain(String domain) {
		// need only class, not implementation and anonymous class is not serializable
		return null;
	}

}
