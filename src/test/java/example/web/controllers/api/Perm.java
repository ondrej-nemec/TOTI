package example.web.controllers.api;

import java.io.Serializable;
import java.util.Arrays;

import toti.security.permissions.Permissions;
import toti.security.permissions.Rule;
import toti.security.permissions.Rules;

public class Perm implements Permissions, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Rules getRulesForDomain(String domain) {
		if (domain.equals("test1")) {
			return new Rules(null, Arrays.asList(
				new Rule(toti.security.Action.UPDATE, ()->Arrays.asList())
			));
		}
		if (domain.equals("test2")) {
			return new Rules(null, Arrays.asList(
				new Rule(toti.security.Action.UPDATE, ()->Arrays.asList())
			));
		}
		return new Rules(new Rule(toti.security.Action.ADMIN, ()->Arrays.asList()), Arrays.asList());
	}
}
