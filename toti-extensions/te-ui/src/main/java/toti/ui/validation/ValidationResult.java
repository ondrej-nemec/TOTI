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
	
	public boolean isValid(String param) {
		if (!errors.containsKey(param)) {
			return true;
		}
		return errors.get(param).isEmpty();
	}
	
	public ValidationResult addSubResult(ValidationResult subResult) {
		this.errors.putAll(subResult.errors);
		return this;
	}
	
	public ValidationResult addError(String onError) {
		return addError("form", onError);
	}
	
	public ValidationResult addError(String propertyName, String errorMessage) {
		if (errors.get(propertyName) == null) {
			errors.put(propertyName, new HashSet<>());
		}
		errors.get(propertyName).add(errorMessage);
		return this;
	}
	
	@Override
	public Object toJson() {
		return errors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ValidationResult other = (ValidationResult) obj;
		if (errors == null) {
			if (other.errors != null) {
				return false;
			}
		} else if (!errors.equals(other.errors)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ValidationResult [errors=" + errors + "]";
	}
	
}
