package toti.extension.validation.rules;

import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class MinValueRule implements Rule {

	private final Number minValue;
	private final Supplier<String> onError;

	public MinValueRule(Number minValue, Supplier<String> onError) {
		this.minValue = minValue;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		if (!isOk(item.getParsedValue())) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

	private boolean isOk(Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value == null || minValue.doubleValue() <= value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return false;
		}
	}

}
