package toti.ui.validation.rules;

import toti.answers.request.Request;
import toti.ui.validation.ValidationItem;

public interface Rule {
	
	void check(Request request, String propertyName, String ruleName, ValidationItem item);
	
}
