package toti.ui.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ji.json.Jsonable;

public class ValidationResult implements Jsonable {

	private Map<String, Set<String>> errors = new HashMap<>();
	
	public boolean isValid() {
		return errors.isEmpty();
	}
	
	public void addSubResult(ValidationResult subResult) {
		this.errors.putAll(subResult.errors);
	}
	
	public void addError(String onError) {
		addError("form", onError);
	}
	
	public void addError(String propertyName, String errorMessage) {
		if (errors.get(propertyName) == null) {
			errors.put(propertyName, new HashSet<>());
		}
		errors.get(propertyName).add(errorMessage);
	}
	
	@Override
	public Object toJson() {
		return errors;
	}
	
}
