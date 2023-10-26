package toti.ui.validation.rules;

import toti.ui.validation.ValidationItem;

public interface Rule {
	
	void check(String propertyName, String ruleName, ValidationItem item);
	
}
