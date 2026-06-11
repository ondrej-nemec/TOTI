package toti.extension.validation.rules;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class MaxLengthRule implements Rule {

	private final Integer maxLength;
	private final Supplier<String> onError;

	public MaxLengthRule(Integer maxLength, Supplier<String> onError) {
		this.maxLength = maxLength;
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
		if (o == null) {
			return false;
		}
		DictionaryValue dicVal = new DictionaryValue(o);
		if (dicVal.is(Map.class)) {
			return maxLength < dicVal.getMap().size();
		}
		if (dicVal.is(List.class)) {
			return maxLength < dicVal.getList().size();
		}
		return maxLength < o.toString().length();
	}

}
