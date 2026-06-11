package toti.extension.validation.results;

public class CheckResult {

	private final boolean isMoreValidationPossible;

	public CheckResult(boolean isMoreValidationPossible) {
		this.isMoreValidationPossible = isMoreValidationPossible;
	}

	public boolean isMoreValidationPossible() {
		return isMoreValidationPossible;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isMoreValidationPossible ? 1231 : 1237);
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
		CheckResult other = (CheckResult) obj;
		if (isMoreValidationPossible != other.isMoreValidationPossible)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CheckResult [isMoreValidationPossible=" + isMoreValidationPossible + "]";
	}


	
}
