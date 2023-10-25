package toti.ui.validation.rule;

import java.util.function.Function;

import ji.translator.Translator;
import toti.ui.validation.ValidationItem;

public class RequiredItemRule implements Rule {
	
	private final boolean isRequired;
	private final Function<Translator, String> onError;
	
	public RequiredItemRule(boolean isRequired, Function<Translator, String> onError) {
		this.onError = onError;
		this.isRequired = isRequired;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		if (isRequired && item.getOriginValue() == null) {
			item.addError(propertyName, onError);
		}
		// not continue with null value
		item.setCanValidate(false);
	}
}
