package toti.validation.rules;

import java.util.Collection;
import java.util.function.Function;

import toti.extensions.Translator;
import toti.validation.ValidationItem;

public class AllowedValuesRule extends SimpleRule<Collection<Object>> {

	public AllowedValuesRule(Collection<Object> value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Collection<Object> allowedList, Object o) {
		return !allowedList.contains(o);
	}
	
	@Override
	protected Object getValue(ValidationItem item) {
		return item.getNewValue();
	}

}
