package toti.validation.rules;

import toti.answers.action.RequestInterruptedException;
import toti.answers.request.Request;
import toti.validation.ValidationItem;

public interface Rule {
	
	void check(Request request, String propertyName, String ruleName, ValidationItem item) throws RequestInterruptedException;
	
}
