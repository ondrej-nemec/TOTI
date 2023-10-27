package ext.grid;

public class Filter {

	private FilterMode mode;
	private String name;
	private Object value;

    private boolean isCI;
    private boolean ignoreDiacritics;
    
	public Filter() {}

	public Filter(String name, FilterMode mode, Object value, boolean isCI, boolean isIgnoreDiactritics) {
		this.mode = mode;
		this.name = name;
		this.value = value;
		this.isCI = isCI;
		this.ignoreDiacritics = isIgnoreDiactritics;
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
	
	public boolean isCI() {
		return isCI;
	}
	
	public boolean isIgnoreDiacritics() {
		return ignoreDiacritics;
	}

	@Override
	public String toString() {
		return "Filter [mode=" + mode + ", name=" + name + ", value=" + value + "]";
	}
	
}
