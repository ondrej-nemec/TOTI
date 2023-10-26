package toti.ui.validation.rules;

import java.util.function.BiFunction;

import ji.translator.Translator;
import toti.ui.validation.ValidationItem;

public class RequiredItemRule implements Rule {
	
	private final boolean isRequired;
	private final BiFunction<Translator, String, String> onError;
	
	public RequiredItemRule(boolean isRequired, BiFunction<Translator, String, String> onError) {
		this.onError = onError;
		this.isRequired = isRequired;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		if (isRequired && item.getOriginValue() == null) {
			item.addError(propertyName, (translator)->onError.apply(translator, ruleName));
		} else if (isRequired && item.getNewValue() == null) {
			// after retype empty string is null number
			item.addError(propertyName, (translator)->onError.apply(translator, ruleName));
		}
		if (item.getOriginValue() == null || item.getNewValue() == null) {
			// not continue with null value
			item.setCanValidate(false);
		}
	}
}
