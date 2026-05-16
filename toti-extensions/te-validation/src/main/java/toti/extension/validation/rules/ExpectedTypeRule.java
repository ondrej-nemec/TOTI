package toti.extension.validation.rules;

import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.extension.validation.ValidationItem;
import toti.lib.common.structures.DictionaryValue;

public class ExpectedTypeRule implements Rule {
	
	private final Class<?> expectedType;
	private final Function<Translator, String> onError;
	
	public ExpectedTypeRule(Class<?> expectedType, Function<Translator, String> onError) {
		this.expectedType = expectedType;
		this.onError = onError;
	}
	
	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		try {
			Object newO = new DictionaryValue(item.getOriginValue()).getValue(expectedType);
			item.setNewValue(newO);
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(propertyName, onError);
			item.setCanValidate(false);
		}
	}

}
