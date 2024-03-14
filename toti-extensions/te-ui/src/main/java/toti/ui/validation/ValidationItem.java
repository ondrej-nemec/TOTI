package toti.ui.validation;

import java.util.function.Function;

import ji.translator.Translator;
import toti.answers.request.Identity;

public class ValidationItem {

	private final String name;
	private final Object originValue;
	private final ValidationResult result;
	private final Translator translator;
	private final Identity identity;
	
	private Object newValue;
	private boolean canValidate;
	
	public ValidationItem(String name, Object originValue, ValidationResult result, Translator translator, Identity identity) {
		this.name = name;
		this.originValue = originValue;
		this.newValue = originValue;
		this.canValidate = true;
		this.result = result;
		this.translator = translator;
		this.identity = identity;
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
	
	public void addError(Function<Translator, String> onError) {
		addError(name, onError);
	}
	
	public Translator getTranslator() {
		return translator;
	}
	
	public Identity getIdentity() {
		return identity;
	}

}
