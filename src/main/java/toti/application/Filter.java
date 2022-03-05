package toti.application;

public class Filter {

	private FilterMode mode;
	private String name;
	private Object value;
	
	public Filter() {}

	public Filter(String name, FilterMode mode, Object value) {
		this.mode = mode;
		this.name = name;
		this.value = value;
	}

	public FilterMode getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Filter [mode=" + mode + ", name=" + name + ", value=" + value + "]";
	}
	
}
