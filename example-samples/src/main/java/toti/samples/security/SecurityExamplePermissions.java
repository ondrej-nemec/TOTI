package toti.samples.security;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import toti.security.Action;
import toti.security.permissions.Permissions;
import toti.security.permissions.Rule;
import toti.security.permissions.Rules;

public class SecurityExamplePermissions implements Permissions {
	private static final long serialVersionUID = 1L;
	
	private final String userName;
	
	public SecurityExamplePermissions(String userName) {
		this.userName = userName;
	}
	
	@Override
	public Rules getRulesForDomain(String domain) {
		if ("user1".equals(userName)) {
			return getUser1(domain);
		}
		if ("user2".equals(userName)) {
			return getUser2(domain);
		}
		if ("user3".equals(userName)) {
			return getUser3(domain);
		}
		return null;
	}

	private Rules getUser3(String domain) {
		List<Rule> rules = new LinkedList<>();
		if (SecurityExample.DOMAIN_1.equals(domain)) {
			rules.add(new Rule(Action.ADMIN, ()->Arrays.asList(
				"d1_1","d1_2","d1_3","d1_4"
			)));
		}
		if (SecurityExample.DOMAIN_2.equals(domain)) {
			rules.add(new Rule(Action.ADMIN, ()->Arrays.asList(
				"d1_2","d2_2","d2_3","d2_4"
			)));
		}
		return new Rules(rules);
	}

	private Rules getUser2(String domain) {
		List<Rule> rules = new LinkedList<>();
		if (SecurityExample.DOMAIN_1.equals(domain)) {
			rules.add(new Rule(Action.CREATE, ()->Arrays.asList(
				"d1_1","d1_4"
			)));
		}
		if (SecurityExample.DOMAIN_2.equals(domain)) {
			rules.add(new Rule(Action.CREATE, ()->Arrays.asList(
				"d2_2","d2_4"
			)));
		}
		return new Rules(rules);
	}

	// no rules
	private Rules getUser1(String domain) {
		return new Rules(Arrays.asList());
	}

}
