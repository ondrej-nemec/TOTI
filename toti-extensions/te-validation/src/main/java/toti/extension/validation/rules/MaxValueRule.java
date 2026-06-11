package toti.extension.validation.rules;

import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class MaxValueRule implements Rule {

	private final Number maxValue;
	private final Supplier<String> onError;

	public MaxValueRule(Number maxValue, Supplier<String> onError) {
		this.maxValue = maxValue;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		if (isErrorToShow(item.getParsedValue())) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

	private boolean isErrorToShow(Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value != null && maxValue.doubleValue() < value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return true;
		}
	}
}
