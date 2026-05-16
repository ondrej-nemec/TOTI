package toti.extension.validation.rules;

import toti.extension.validation.ValidationItem;

public interface Rule {
	
	void check(String propertyName, String ruleName, ValidationItem item);
	
}
