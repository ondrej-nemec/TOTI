package ji.querybuilder.structures;

import ji.querybuilder.Escape;

public class DefaultValue {

	private final Object value;
	private final boolean isModify;
	
	public DefaultValue(Object value, boolean isModify) {
		this.value = value;
		this.isModify = isModify;
	}
	
	public boolean isModify() {
		return isModify;
	}

	public String getValue(Escape escape) {
		return escape.escape(value);
	}
	
	// for rename column
	protected Object get() {
		return value;
	}

	@Override
	public String toString() {
		return "DefaultValue [value=" + value + ", isModify=" + isModify + "]";
	}

}
