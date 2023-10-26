package toti.ui.validation;

import java.util.function.Function;

import ji.translator.Translator;

public class ValidationItem {
	
	private final Object originValue;
	private final ValidationResult result;
	private final Translator translator;
	
	private Object newValue;
	private boolean canValidate;
	
	public ValidationItem(Object originValue, ValidationResult result, Translator translator) {
		this.originValue = originValue;
		this.newValue = originValue;
		this.canValidate = true;
		this.result = result;
		this.translator = translator;
	}
	
	public void setCanValidate(boolean canValidate) {
		this.canValidate = canValidate;
	}
	
	public boolean canValidationContinue() {
		return canValidate;
	}
	
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}
	
	public Object getOriginValue() {
		return originValue;
	}
	
	public void addSubResult(ValidationResult subResult) {
		this.result.addSubResult(subResult);
	}
	
	public void addError(String propertyName, Function<Translator, String> onError) {
		this.result.addError(propertyName, onError.apply(translator));
	}

}
