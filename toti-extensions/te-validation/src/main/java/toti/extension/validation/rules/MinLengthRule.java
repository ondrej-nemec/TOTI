package toti.extension.validation.rules;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class MinLengthRule implements Rule {

	private final Integer minLength;
	private final Supplier<String> onError;

	public MinLengthRule(Integer minLength, Supplier<String> onError) {
		this.minLength = minLength;
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
			return minLength > dicVal.getMap().size();
		}
		if (dicVal.is(List.class)) {
			return minLength > dicVal.getList().size();
		}
		return minLength > o.toString().length();
	}

}
