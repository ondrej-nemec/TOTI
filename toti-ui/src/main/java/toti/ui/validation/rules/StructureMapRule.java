package toti.ui.validation.rules;

import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.Validator;

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
			item.addSubResult(validator.validate(propertyName + "[%s]", fields, null));
			item.setNewValue(fields);
		} catch (NullPointerException | ClassCastException e) {
			item.addError(propertyName, onError);
		}
	}
}
