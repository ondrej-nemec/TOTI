package toti.extension.validation.results;

import toti.lib.common.structures.dictionary.Scalar;

public interface CustomValueValidationItem extends Scalar {

	Object getRawValue();

	@Override
	default Object _getValue() {
		return getParsedValue();
	}

	Object getParsedValue();

	void addError(String text);

	void setValue(Object value);

}
