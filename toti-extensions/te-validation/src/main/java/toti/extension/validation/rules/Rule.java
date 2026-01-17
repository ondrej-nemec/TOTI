package toti.extension.validation.rules;

import toti.answers.request.Request;
import toti.extension.validation.ValidationItem;

public interface Rule {
	
	void check(Request request, String propertyName, String ruleName, ValidationItem item);
	
}
