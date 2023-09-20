package toti.security.permissions;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

import toti.security.Action;

public class Rule implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Action action;
	private final Supplier<List<Object>> owners;
	
	/**
	 * 
	 * @param action that are allowed, if no information use UNDEFINED
	 * @param owners, if no owners use null
	 */
	public Rule(Action action, Supplier<List<Object>> owners) {
		this.action = action;
		this.owners = owners;
	}

	public Action getAction() {
		return action;
	}

	public Supplier<List<Object>> getOwners() {
		return owners;
	}
	
}
