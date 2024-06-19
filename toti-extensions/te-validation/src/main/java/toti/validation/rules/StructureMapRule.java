package toti.validation.rules;

import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import toti.extensions.Translator;
import toti.http.http.structures.RequestParameters;
import toti.validation.ValidationItem;
import toti.validation.Validator;
import toti.answers.action.RequestInterruptedException;
import toti.answers.request.Request;

public class StructureMapRule implements Rule {

	private final Validator validator;
	private final Function<Translator, String> onError;

	public StructureMapRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) throws RequestInterruptedException {
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
