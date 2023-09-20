package ext;

import ji.json.Jsonable;

public class Help implements Jsonable {

	private final Object value;
	private final Object title;
	private final String optgroup;
	private final boolean disabled;
	
	public Help(Object value, Object title) {
		this(value, title, null, false);
	}
	
	public Help(Object value, Object title, String optgroup, boolean disabled) {
		this.value = value;
		this.title = title;
		this.optgroup = optgroup;
		this.disabled = disabled;
	}

	public Object getValue() {
		return value;
	}

	public Object getTitle() {
		return title;
	}

	public String getOptgroup() {
		return optgroup;
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public String toString() {
		return "Help [value=" + value + ", title=" + title + ", optgroup=" + optgroup + ", disabled=" + disabled + "]";
	}
	
}
