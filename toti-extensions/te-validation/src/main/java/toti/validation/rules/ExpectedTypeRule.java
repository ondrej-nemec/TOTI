package toti.validation.rules;

import java.util.function.Function;

import toti.extensions.Translator;
import toti.lib.common.structures.DictionaryValue;
import toti.validation.ValidationItem;
import toti.answers.request.Request;

public class ExpectedTypeRule implements Rule {
	
	private final Class<?> expectedType;
	private final Function<Translator, String> onError;
	
	public ExpectedTypeRule(Class<?> expectedType, Function<Translator, String> onError) {
		this.expectedType = expectedType;
		this.onError = onError;
	}
	
	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) {
		try {
			Object newO = new DictionaryValue(item.getOriginValue()).getValue(expectedType);
			item.setNewValue(newO);
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(propertyName, onError);
			item.setCanValidate(false);
		}
	}

}
