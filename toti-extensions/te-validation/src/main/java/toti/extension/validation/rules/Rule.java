package toti.extension.validation.rules;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;

public interface Rule {
	
	CheckResult check(ValidationItem item);
	
}
