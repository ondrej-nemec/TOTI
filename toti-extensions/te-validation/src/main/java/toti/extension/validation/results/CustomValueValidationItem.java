package toti.extension.validation.results;

import toti.lib.common.structures.NonFixedType;

public interface CustomValueValidationItem extends NonFixedType {

	Object getRawValue();

	@Override
	default Object _getValue() {
		return getParsedValue();
	}

	Object getParsedValue();

	void addError(String text);

	void setValue(Object value);

}
