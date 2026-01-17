package toti.extension.validation.rules;

import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.tcpip.structures.RequestParameters;
import toti.application.answers.request.Request;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.Validator;

public class StructureMapRule implements Rule {

	private final Validator validator;
	private final Function<Translator, String> onError;

	public StructureMapRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) {
		try {
			RequestParameters fields = new RequestParameters();
			fields.putAll(new DictionaryValue(item.getOriginValue()).getMap());
			item.addSubResult(validator.validate(
				request, propertyName + "[%s]", fields, item.getTranslator(), item.getIdentity()
			));
			item.setNewValue(fields);
		} catch (NullPointerException | ClassCastException e) {
			item.addError(propertyName, onError);
		}
	}
}
