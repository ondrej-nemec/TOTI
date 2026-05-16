package toti.extension.validation.rules;

import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.Validator;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureMapRule implements Rule {

	private final Validator validator;
	private final Function<Translator, String> onError;

	public StructureMapRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		try {
			RequestParameters fields = new RequestParameters();
			fields.putAll(new DictionaryValue(item.getOriginValue()).getMap());
			item.addSubResult(validator.validate(
				propertyName + "[%s]", fields, item.getTranslator()
			));
			item.setNewValue(fields);
		} catch (NullPointerException | ClassCastException e) {
			item.addError(propertyName, onError);
		}
	}
}
