package toti.validation.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import toti.extensions.Translator;
import toti.http.http.structures.RequestParameters;
import toti.validation.ValidationItem;
import toti.validation.Validator;
import toti.answers.request.Request;

public class StructureListRule implements Rule {

	private final Validator validator;
	private final Function<Translator, String> onError;

	public StructureListRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) {
		try {
			List<Object> list = new DictionaryValue(item.getOriginValue()).getList();
			RequestParameters fields = new RequestParameters();
			for (int i = 0; i < list.size(); i++) {
				fields.put(i + "", list.get(i));
			}
			item.addSubResult(validator.validate(
				request,
				(propertyName.contains(":") ? "" : "%s:") + propertyName + "[]",
				fields,
				item.getTranslator(),
				item.getIdentity()
			));
			item.setNewValue(new ArrayList<>(fields.values()));
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(propertyName, onError);
		}
	}

}
