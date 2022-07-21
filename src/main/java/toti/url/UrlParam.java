package toti.url;

public class UrlParam {
	
	public static final String PARAM_REGEX = "([a-zA-Z0-9_.-]*)";
		
	private final Object value;
	
	private final String name;

	private final boolean isRegex;
	
	public UrlParam(Object value) {
		this(null, value, false);
	}

	protected UrlParam(boolean isRegex) {
		this(null, PARAM_REGEX, isRegex);
	}
	
	public UrlParam(String name, Object value) {
		this(name, value, false);
	}
	
	private UrlParam(String name, Object value, boolean isRegex) {
		this.name = name;
		this.value = value;
		this.isRegex = isRegex;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public boolean isRegex() {
		return isRegex;
	}

	@Override
	public String toString() {
		return "Param [value=" + value + ", name=" + name + ", isRegex=" + isRegex + "]";
	}
	
}
