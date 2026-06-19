package toti.extension.validation.rules;

import java.util.Map;
import java.util.function.Supplier;

import toti.extension.validation.Validator;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureMapRule implements Rule {

	private final Validator validator;
	private final Supplier<String> onError;

	public StructureMapRule(Validator validator, Supplier<String> onError) {
		this.validator = validator;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		try {
			RequestParameters fields = new RequestParameters();
			fields.putAll(new DictionaryValue(item.getRawValue()).getMap());
			
			ValidationCollection validationCollection = validator._validate(
				item.getExtendedName() + "[%s]", fields, item.getOriginName(), item.getExtendedName()
			);
			Map<String, Object> subErrors = validationCollection.getErrors();
			if (!subErrors.isEmpty()) {
				item.addError(subErrors);	
			}
			item.setValue(validationCollection.getValues());
			return new CheckResult(true);
		} catch (NullPointerException | ClassCastException e) {
			item.addError(onError.get());
			return new CheckResult(false);
		}
	}
}
