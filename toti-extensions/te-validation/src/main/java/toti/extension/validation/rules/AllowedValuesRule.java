package toti.extension.validation.rules;

import java.util.Collection;
import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;

public class AllowedValuesRule implements Rule {

	private final Collection<Object> allowedList;
	private final Supplier<String> onError;

	public AllowedValuesRule(Collection<Object> allowedList, Supplier<String> onError) {
		this.allowedList = allowedList;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		if (item.getParsedValue() != null && !allowedList.contains(item.getParsedValue())) {
			item.addError(onError.get());
			return new CheckResult(false);
		}
		return new CheckResult(true);
	}

}
