package toti.ui.validation.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.Validator;

public class StructureListRule implements Rule {

	private final Validator validator;
	private final Function<Translator, String> onError;

	public StructureListRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		try {
			List<Object> list = new DictionaryValue(item.getOriginValue()).getList();
			RequestParameters fields = new RequestParameters();
			for (int i = 0; i < list.size(); i++) {
				fields.put(i + "", list.get(i));
			}
			item.addSubResult(validator.validate(
				(propertyName.contains(":") ? "" : "%s:") + propertyName + "[]",
				fields,
				item.getTranslator()
			));
			item.setNewValue(new ArrayList<>(fields.values()));
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(propertyName, onError);
		}
	}

}
