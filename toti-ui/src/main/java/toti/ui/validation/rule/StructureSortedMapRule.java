package toti.ui.validation.rule;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.common.structures.SortedMap;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.Validator;

public class StructureSortedMapRule implements Rule {
	
	private final Validator validator;
	private final Function<Translator, String> onError;
	
	public StructureSortedMapRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(String propertyName, String ruleName, ValidationItem item) {
		try {
			RequestParameters fields = new RequestParameters();
			for (Object value : new DictionaryValue(item.getOriginValue()).getList()) {
				DictionaryValue dvItem = new DictionaryValue(value);
				if (!dvItem.is(Map.class) || dvItem.getMap().size() != 1) {
					item.addError(propertyName, onError);
					return;
				}
				Entry<Object, Object> entryItem = dvItem.getMap().entrySet().iterator().next();
				fields.put(entryItem.getKey().toString(), entryItem.getValue());
			}
			item.addSubResult(validator.validate(propertyName + "[%s]", fields, null));
			item.setNewValue(new SortedMap<String, Object>().putAll(fields.toMap()));
		} catch (Exception e) {
			item.addError(propertyName, onError);
		}
	}
}
