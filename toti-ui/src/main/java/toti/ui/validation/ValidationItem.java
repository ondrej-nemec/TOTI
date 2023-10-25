package toti.ui.validation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import ji.translator.Translator;

public class ValidationItem {
	
	private final Object originValue;
	private final String name;
	
	private Object newValue;
	private boolean canValidate;
	
	public ValidationItem(String name, Object originValue) {
		this.name = name;
		this.originValue = originValue;
		this.newValue = originValue;
		this.canValidate = true;
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
	
	public boolean isValid() {
		return false; // TODO if no errors
	}
	
	public void addSubResult(Map<String, Set<String>> errors) {
		// TODO temporal
	}
	
	public void addSubResult(ValidationItem subResult) {
		// TODO add all
	}
	
	public void addError(String propertyName, Function<Translator, String> onError) {
		/*
		if (errors.get(propertyName) == null) {
				errors.put(propertyName, new HashSet<>());
			}
			errors.get(propertyName).add(onError.apply(translator));
		*/
	}

}
