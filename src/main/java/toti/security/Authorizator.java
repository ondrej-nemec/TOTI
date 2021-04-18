package toti.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Logger;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.security.permissions.Permissions;
import toti.security.permissions.Rule;
import toti.security.permissions.Rules;

public class Authorizator {
	
	private final Logger logger;
	
	public Authorizator(Logger logger) {
		this.logger = logger;
	}
	

	public void authorize(User who, String where, Action what) {
		who.setAllowedIds(authorize(who.getPermissions(), who.getId(),  where, what));
	}
	
	// TODO test this method
	protected Set<Object> authorize(Permissions permissions, Object who, String where, Action what) {
		logger.debug("Access required: " + who + " -> " + where + " -> " + what);
		
		if (what == Action.FORBIDDEN || what == Action.UNDEFINED) {
			throw new NotAllowedActionException(what);
		}
		
		Rules rules = permissions.getRulesForDomain(where);
		if (rules == null) {
			logger.warn("No access rule found for: " + who + " -> " + where + " -> " + what); 
			throw new NotAllowedActionException("No rule found");
		}
		Set<Object> allowedIds = new HashSet<>();
		Set<Object> forbidden = new HashSet<>();
		Action action = Action.UNDEFINED;
		List<Rule> otherRules = rules.getRules();
		if (otherRules != null) {
			for (Rule rule : otherRules) {
				if (rule.getOwners() == null) {
					throw new NotAllowedActionException("Owners for Role Rule are not defined");
				}
				if (rule.getAction() == Action.FORBIDDEN) {
					forbidden.addAll(rule.getOwners().get());
				} else if (isAllowed(rule.getAction(), what)) {
					action = selectRole(action, rule.getAction());
					allowedIds.addAll(rule.getOwners().get());
				}
			}
		}
		allowedIds.removeAll(forbidden);
		Rule privilegedRule = rules.getPrivilegedRule();
		if (privilegedRule.getAction() != Action.UNDEFINED) {
			if (privilegedRule.getOwners() == null) {
				throw new NotAllowedActionException("Owners for User Rule are not defined");
			}
			if (privilegedRule.getAction() == Action.FORBIDDEN) {
				allowedIds.removeAll(privilegedRule.getOwners().get());
			} else if (isAllowed(privilegedRule.getAction(), what)) {
				allowedIds.addAll(privilegedRule.getOwners().get());
				action = privilegedRule.getAction();
			}
		}
		if (action == Action.UNDEFINED) {
			logger.warn("No access rule for: " + who + " -> " + where + " -> " + what);
			throw new AccessDeniedException(who, where, what);
		}
		if (action == Action.FORBIDDEN) {
			throw new AccessDeniedException(who, where, what);
		}
		return allowedIds;
	}

	public boolean isAllowed(User who, String where, Action what) {
		if (who == null) {
			return false;
		}
		return authorize(who.getPermissions(), who.getId(), where, what, null);
	}

	public void authorize(User who, String where, Action what, Object owner) {
		if (!authorize(who.getPermissions(), who.getId(), where, what, owner)) {
			throw new AccessDeniedException(who, where, what, owner);
		}
	}

	// TODO test this method
	protected boolean authorize(Permissions permissions, Object who, String where, Action what, Object owner) {
		logger.debug("Access required: " + who + " -> " + where + " -> " + what + " (" + owner + ")");
		if (what == Action.FORBIDDEN || what == Action.UNDEFINED) {
			throw new NotAllowedActionException(what);
		}
		
		Rules rules = permissions.getRulesForDomain(where);
		if (rules == null) {
			logger.warn("No access rule found for: " + who + " -> " + where + " -> " + what + " (" + owner + ")"); 
			return false;
		}
		Rule privileged = rules.getPrivilegedRule();
		if (privileged != null && privileged.getAction() != Action.UNDEFINED) {
			boolean owned = owner != null && privileged.getOwners() != null ? privileged.getOwners().get().contains(owner) : true;
			return isAllowed(privileged.getAction(), what) && owned;
		}
		
		Action action = Action.UNDEFINED;
		List<Rule> otherRules = rules.getRules();
		if (otherRules != null) {
			for (Rule rule : otherRules) {
				boolean owned = owner != null && rule.getOwners() != null ? rule.getOwners().get().contains(owner) : true;
				action = owned ? selectRole(action, rule.getAction()) : Action.UNDEFINED;
			}
		}
		
		if (action != Action.UNDEFINED) {
			return isAllowed(action, what);
		}
		
		logger.warn("No access rule for: " + who + " -> " + where + " -> " + what + " (" + owner + ")"); 
		return false;
	}
	
	private Action selectRole(Action origin, Action actual) {
		if (origin.ordinal() >= actual.ordinal()) {
			return origin;
		}
		return actual;
	}
	
	private boolean isAllowed(Action finded, Action asked) {
		if (finded != Action.FORBIDDEN) {
			return finded.ordinal() <= asked.ordinal();
		}
		return false;
	}
	
}
