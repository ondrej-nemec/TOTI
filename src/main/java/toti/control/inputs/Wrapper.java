package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Wrapper {
	
	// both
	private final String name;
	private final String id;
	private final String type;
	private final Map<String, String> params = new HashMap<>();
	
	private String value = null;

	// input
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private Boolean exclude = null;
	private Boolean editable = null;
	private String placeholder = null;
	
	// filter
	// ...
	
	public Wrapper(String type, String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.required = required;
		this.type = type;
	}
	
	/********************/

	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		set.put("id", id);
		set.putAll(params);
		if (value != null) {
			set.put("value", value);
		}
		if (placeholder != null) {
			set.put("placeholder", placeholder);
		}
		return set;
	}
	
	public Map<String, Object> getInputSettings() {
		return getInputSettings(getFilterSettings());
	}

	public Map<String, Object> getInputSettings(Map<String, Object> set) {
		set.put("name", name);
		set.put("type", type);
		if (required) {
			set.put("required", required);
		}
		if (disabled) {
			set.put("disabled", disabled);
		}
		if (exclude != null) {
			set.put("exclude", exclude);
		}
		if (editable != null) {
			set.put("editable", editable);
		}
		if (title != null) {
			set.put("title", title);
		}
		return set;
	}
	
	/**************************/
	
	public String getType() {
		return type;
	}
	
	public void addParam(String name, String value) {
		params.put(name, value);
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDefaultValue(Object value) {
		if (value != null) {
			this.value = value.toString();
		} else {
			this.value = null;
		}
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (exclude == null) {
			exclude = disabled;
		}
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
