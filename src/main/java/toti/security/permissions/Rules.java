package toti.security.permissions;

import java.util.List;

public class Rules {

	private final Rule privilegedAction;
	
	private final List<Rule> actions;

	public Rules(Rule privilegedAction, List<Rule> actions) {
		this.privilegedAction = privilegedAction;
		this.actions = actions;
	}

	public Rule getPrivilegedRule() {
		return privilegedAction;
	}

	public List<Rule> getRules() {
		return actions;
	}
	
}
