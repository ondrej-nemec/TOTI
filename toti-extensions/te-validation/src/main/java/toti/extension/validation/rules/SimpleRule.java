package toti.extension.validation.rules;

import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.application.answers.request.Request;
import toti.extension.validation.ValidationItem;

public abstract class SimpleRule<T> implements Rule {

	private final T value;
	private final Function<Translator, String> onError;
	
	public SimpleRule(T value, Function<Translator, String> onError) {
		this.value = value;
		this.onError = onError;
	}

	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) {
		if (isErrorToShow(value, getValue(item))) {
			item.addError(propertyName, onError);
		}
	}
	
	protected Object getValue(ValidationItem item) {
		return item.getOriginValue();
	}
	
	abstract protected boolean isErrorToShow(T value, Object o);
	
}
