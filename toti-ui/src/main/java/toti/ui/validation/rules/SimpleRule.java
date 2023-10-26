package toti.ui.validation.rules;

import java.util.function.Function;

import ji.translator.Translator;
import toti.ui.validation.ValidationItem;

public abstract class SimpleRule<T> implements Rule {

	private final T value;
	private final Function<Translator, String> onError;
	
	public SimpleRule(T value, Function<Translator, String> onError) {
		this.value = value;
		this.onError = onError;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		if (isErrorToShow(value, getValue(item))) {
			item.addError(propertyName, onError);
		}
	}
	
	protected Object getValue(ValidationItem item) {
		return item.getOriginValue();
	}
	
	abstract protected boolean isErrorToShow(T value, Object o);
	
}
