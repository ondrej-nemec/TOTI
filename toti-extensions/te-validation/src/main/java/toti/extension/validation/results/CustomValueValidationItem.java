package toti.extension.validation.results;

public interface CustomValueValidationItem {

	Object getRawValue();

	Object getParsedValue();

	void addError(String text);

	void setValue(Object value);

}
