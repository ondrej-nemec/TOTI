package toti.extension.validation.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import toti.extension.validation.Validator;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureListRule implements Rule {

	private final Validator validator;
	private final Supplier<String> onError;

	public StructureListRule(Validator validator, Supplier<String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		try {
			List<Object> list = new DictionaryValue(item.getRawValue()).getList();
			RequestParameters fields = new RequestParameters();
			for (int i = 0; i < list.size(); i++) {
				fields.put(i + "", list.get(i));
			}
			ValidationCollection validationCollection = validator._validate(
				(item.getExtendedName().contains(":") ? "" : "%s") + item.getExtendedName() + "[]",
				fields, item.getOriginName(), item.getExtendedName()
			);
			Map<String, Object> subErrors = validationCollection.getErrors();
			if (!subErrors.isEmpty()) {
				item.addError(subErrors);	
			}
			List<Object> result = new LinkedList<>();
			fields.forEach((key, value)->{
				result.add(validationCollection.getValue(key));
			});
			item.setValue(result);
			return new CheckResult(true);
		} catch (ClassCastException | NumberFormatException e) {
			item.addError(onError.get());
			return new CheckResult(false);
		}
	}

}
