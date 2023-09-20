package toti.samples.sign;

import java.util.Arrays;

import toti.security.permissions.Permissions;
import toti.security.permissions.Rules;

/**
 * Empty implementation for just sign example
 * @author Ondřej Němec
 *
 */
public class SignExamplePermissions implements Permissions {

	private static final long serialVersionUID = 1L;

	@Override
	public Rules getRulesForDomain(String domain) {
		return new Rules(null, Arrays.asList());
	}

}
