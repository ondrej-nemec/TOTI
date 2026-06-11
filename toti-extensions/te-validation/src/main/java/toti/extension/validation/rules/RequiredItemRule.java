package toti.extension.validation.rules;

import java.util.function.Function;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;

public class RequiredItemRule implements Rule {
	
	private final boolean isRequired;
	private final Function<String, String> onError;
	
	public RequiredItemRule(boolean isRequired, Function<String, String> onError) {
		this.onError = onError;
		this.isRequired = isRequired;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		// not continue with null value
		if (isRequired && item.getRawValue() == null) {
			item.addError(onError.apply(item.getOriginName()));
			return new CheckResult(false);
		} else if (isRequired && item.getParsedValue() == null) {
			// after retype empty string is null number
			item.addError(onError.apply(item.getOriginName()));
			return new CheckResult(false);
		}
		return new CheckResult(true);
	}
}
