package toti.extension.validation.results;

import java.util.HashSet;
import java.util.Set;

public class ValidationItem implements CustomValueValidationItem {

	private final String originName;
	private final String extendedName;
	private final Object rawValue;
	private final Set<Object> errors;
	private Object parsedValue;

	public ValidationItem(String originName, String extendedName, Object rawValue) {
		this(originName, extendedName, rawValue, rawValue);
	}

	public ValidationItem(String originName, String extendedName, Object rawValue, Object parsedValue) {
		this.originName = originName;
		this.extendedName = extendedName;
		this.rawValue = rawValue;
		this.parsedValue = parsedValue;
		this.errors = new HashSet<>();
	}

	@Override
	public void addError(String error) {
		this.errors.add(error);
	}

	public void addError(Object error) {
		this.errors.add(error);
	}

	@Override
	public void setValue(Object parsedValue) {
		this.parsedValue = parsedValue;
	}

	public String getOriginName() {
		return originName;
	}

	public String getExtendedName() {
		return extendedName;
	}

	public Set<Object> getErrors() {
		return errors;
	}

	@Override
	public Object getParsedValue() {
		return parsedValue;
	}

	@Override
	public Object getRawValue() {
		return rawValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((originName == null) ? 0 : originName.hashCode());
		result = prime * result + ((extendedName == null) ? 0 : extendedName.hashCode());
		result = prime * result + ((rawValue == null) ? 0 : rawValue.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((parsedValue == null) ? 0 : parsedValue.hashCode());
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
		ValidationItem other = (ValidationItem) obj;
		if (originName == null) {
			if (other.originName != null)
				return false;
		} else if (!originName.equals(other.originName))
			return false;
		if (extendedName == null) {
			if (other.extendedName != null)
				return false;
		} else if (!extendedName.equals(other.extendedName))
			return false;
		if (rawValue == null) {
			if (other.rawValue != null)
				return false;
		} else if (!rawValue.equals(other.rawValue))
			return false;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (parsedValue == null) {
			if (other.parsedValue != null)
				return false;
		} else if (!parsedValue.equals(other.parsedValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ValidationItem [originName=" + originName + ", extendedName=" + extendedName + ", rawValue=" + rawValue
				+ ", errors=" + errors + ", parsedValue=" + parsedValue + "]";
	}

}
