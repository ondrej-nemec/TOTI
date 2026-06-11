package toti.extension.validation.rules;

import java.util.function.Consumer;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.CustomValueValidationItem;
import toti.extension.validation.results.ValidationItem;

public class CustomValidationRule implements Rule {

	private final Consumer<CustomValueValidationItem> rule;

	public CustomValidationRule(Consumer<CustomValueValidationItem> rule) {
		this.rule = rule;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		rule.accept(item);
		return new CheckResult(true);
	}

}
