package ji.common.structures;

public class BooleanBuilder {
	
	private Boolean value;
	
	public BooleanBuilder() {
		this(null);
	}

	public BooleanBuilder(Boolean value) {
		this.value = value;
	}
	
	public void set(Boolean value) {
		this.value = value;
	}
	
	public Boolean get() {
		return value;
	}
	
	public void and(Boolean value) {
		if (value == null) {
			return;
		}
		if (this.value == null) {
			this.value = value;
		} else {
			this.value = this.value && value;
		}
	}
	
	public void or(Boolean value) {
		if (value == null) {
			return;
		}
		if (this.value == null) {
			this.value = value;
		} else {
			this.value = this.value || value;
		}
		
	}
}
