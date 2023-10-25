package toti.ui.validation.rule;

import toti.ui.validation.ValidationItem;

public interface Rule {
	
	void check(String propertyName, String ruleName, ValidationItem result);
	
}
