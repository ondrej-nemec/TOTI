package toti.extension.validation.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import toti.extension.validation.Validator;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.SortedMap;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureSortedMapRule implements Rule {
	
	private final Validator validator;
	private final Supplier<String> onError;
	
	public StructureSortedMapRule(Validator validator, Supplier<String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		try {
			RequestParameters fields = new RequestParameters();
			List<String> order = new LinkedList<>();
			for (Object value : new DictionaryValue(item.getRawValue()).getList()) {
				DictionaryValue dvItem = new DictionaryValue(value);
				if (!dvItem.is(Map.class) || dvItem.getMap().size() != 1) {
					item.addError(onError.get());
					return new CheckResult(false);
				}
				Entry<Object, Object> entryItem = dvItem.getMap().entrySet().iterator().next();
				fields.put(entryItem.getKey().toString(), entryItem.getValue());
				order.add(entryItem.getKey().toString());
			}
			ValidationCollection validationCollection = validator._validate(
				item.getExtendedName() + "[%s]", fields, item.getOriginName(), item.getExtendedName()
			);
			Map<String, Object> subErrors = validationCollection.getErrors();
			if (!subErrors.isEmpty()) {
				item.addError(subErrors);	
			}
			var removed = new LinkedList<>(order);
			removed.removeAll(validationCollection.getItemsNames());
			order.removeAll(removed);

			var added = new LinkedList<>(validationCollection.getItemsNames());
			added.removeAll(order);
			order.addAll(added);

			SortedMap<String, Object> result = new SortedMap<>();
			order.forEach(key->{
				result.append(key, validationCollection.getValue(key));
			});
			item.setValue(result);
			return new CheckResult(true);
		} catch (Exception e) {
			item.addError(onError.get());
			return new CheckResult(false);
		}
	}
}
