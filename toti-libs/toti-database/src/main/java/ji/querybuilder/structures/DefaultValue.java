package ji.querybuilder.structures;

import ji.querybuilder.Escape;

public class DefaultValue {

	private final Object value;
	
	// null - not used, true - set, false - clear
	private final Boolean mode;
	
	public static DefaultValue set(Object value) {
		return new DefaultValue(value, true);
	}

	public static DefaultValue clear() {
		return new DefaultValue(null, false);
	}

	public static DefaultValue notUse() {
		return new DefaultValue(null, null);
	}
	
	private DefaultValue(Object value, Boolean mode) {
		this.value = value;
		this.mode = mode;
	}
	
	public boolean isUsed() {
		return mode != null;
	}
	
	public boolean isSet() {
		return mode != null && mode;
	}
	
	public boolean isClear() {
		return mode != null && !mode;
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
		return "DefaultValue [value=" + value + ", mode=" + mode + "]";
	}

}
