package toti.ui.backend.help;

import java.util.HashMap;
import java.util.Map;

import ji.json.Jsonable;

public class Help implements Jsonable {

	private final Object value;
	private final Object title;
	private final String optgroup;
	private final boolean disabled;
	private final Map<String, Object> params;
	
	public Help(Object value, Object title) {
		this(value, title, null, false);
	}
	
	public Help(Object value, Object title, String optgroup, boolean disabled) {
		this.value = value;
		this.title = title;
		this.optgroup = optgroup;
		this.disabled = disabled;
		this.params = new HashMap<>();
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
	
	public Help addParameter(String name, Object value) {
		this.params.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return "Help [value=" + value + ", title=" + title + ", optgroup=" + optgroup + ", disabled=" + disabled + "]";
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> params = new HashMap<>(this.params);
		params.put("value", value);
		params.put("title", title);
		params.put("optgroup", optgroup);
		params.put("disabled", disabled);
		return params;
	}
	
}
