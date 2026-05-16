package toti.extension.validation.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.Validator;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.tcpip.structures.RequestParameters;

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
