package toti.extension.validation.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.Validator;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.SortedMap;
import toti.lib.tcpip.structures.RequestParameters;

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
			List<String> order = new LinkedList<>();
			for (Object value : new DictionaryValue(item.getOriginValue()).getList()) {
				DictionaryValue dvItem = new DictionaryValue(value);
				if (!dvItem.is(Map.class) || dvItem.getMap().size() != 1) {
					item.addError(propertyName, onError);
					return;
				}
				Entry<Object, Object> entryItem = dvItem.getMap().entrySet().iterator().next();
				fields.put(entryItem.getKey().toString(), entryItem.getValue());
				order.add(entryItem.getKey().toString());
			}
			item.addSubResult(validator.validate(
				propertyName + "[%s]", fields, item.getTranslator()
			));
			SortedMap<String, Object> result = new SortedMap<>();
			order.forEach(key->{
				result.append(key, fields.get(key));
			});
			item.setNewValue(result);
		} catch (Exception e) {
			item.addError(propertyName, onError);
		}
	}
}
