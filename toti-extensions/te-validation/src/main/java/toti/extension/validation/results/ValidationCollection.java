package toti.extension.validation.results;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import toti.extension.validation.ValidationResult;
import toti.lib.tcpip.structures.RequestParameters;

public class ValidationCollection implements ValidationResult, CustomCollectionValidationItem {

	private final Map<String, Object> subErrors;
	private final Set<Object> ownErrors;
	private final RequestParameters values;

	public ValidationCollection() {
		this(new HashMap<>(), new HashSet<>(), new RequestParameters());
	}

	public ValidationCollection(Map<String, Object> subErrors, Set<Object> ownErrors, RequestParameters values) {
		this.subErrors = subErrors;
		this.ownErrors = ownErrors;
		this.values = values;
	}

	@Override
	public void addError(String error) {
		this.ownErrors.add(error);
	}

	public void addItem(ValidationItem item) {
		if (!item.getErrors().isEmpty()) {
			subErrors.put(item.getOriginName(), item.getErrors());
		}
		values.put(item.getOriginName(), item.getParsedValue());
	}

	@Override
	public Set<String> getItemsNames() {
		return values.keySet();
	}

	@Override
	public RequestParameters getValues() {
		return values;
	}

	@Override
	public boolean isValid() {
		return subErrors.isEmpty() && ownErrors.isEmpty();
	}

	@Override
	public Map<String, Object> getErrors() {
		Map<String, Object> errors = new HashMap<>(subErrors);
		if (!ownErrors.isEmpty()) {
			errors.put("", ownErrors);
		}
		return errors;
	}

	@Override
	public Object getValue(String name) {
		return values.get(name);
	}

	@Override
	public void removeItem(String propertyName) {
		values.remove(propertyName);
	}

	@Override
	public void setItem(String propertyName, Object value) {
		values.put(propertyName, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subErrors == null) ? 0 : subErrors.hashCode());
		result = prime * result + ((ownErrors == null) ? 0 : ownErrors.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationCollection other = (ValidationCollection) obj;
		if (subErrors == null) {
			if (other.subErrors != null)
				return false;
		} else if (!subErrors.equals(other.subErrors))
			return false;
		if (ownErrors == null) {
			if (other.ownErrors != null)
				return false;
		} else if (!ownErrors.equals(other.ownErrors))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ValidationCollection [\nsubErrors=" + subErrors + "\nownErrors=" + ownErrors + "\nvalues=" + values + "\n]";
	}

}
