package toti.extension.validation.rules;

import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class ExpectedTypeRule implements Rule {
	
	private final Class<?> expectedType;
	private final Supplier<String> onError;
	
	public ExpectedTypeRule(Class<?> expectedType, Supplier<String> onError) {
		this.expectedType = expectedType;
		this.onError = onError;
	}
	
	@Override
	public CheckResult check(ValidationItem item) {
		try {
			Object newO = new DictionaryValue(item.getRawValue()).getValue(expectedType);
			item.setValue(newO);
			return new CheckResult(true);
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(onError.get());
			return new CheckResult(false);
		}
	}

}
