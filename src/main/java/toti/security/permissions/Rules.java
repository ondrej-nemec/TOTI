package toti.security.permissions;

import java.io.Serializable;
import java.util.List;

public class Rules implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Rule privilegedAction;
	
	private final List<Rule> actions;

	public Rules(List<Rule> actions) {
		this(null, actions);
	}
	
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
