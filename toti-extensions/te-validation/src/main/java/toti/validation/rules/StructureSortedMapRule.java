package toti.validation.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import toti.extensions.Translator;
import toti.http.structures.RequestParameters;
import toti.validation.ValidationItem;
import toti.validation.Validator;
import toti.answers.request.Request;
import toti.common.structures.DictionaryValue;
import toti.common.structures.SortedMap;

public class StructureSortedMapRule implements Rule {
	
	private final Validator validator;
	private final Function<Translator, String> onError;
	
	public StructureSortedMapRule(Validator validator, Function<Translator, String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public void check(Request request, String propertyName, String ruleName, ValidationItem item) {
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
				request, propertyName + "[%s]", fields, item.getTranslator(), item.getIdentity()
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
