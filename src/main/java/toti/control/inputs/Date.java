package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.columns.Filter;

public class Date implements Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Date input(String name, boolean required) {
		return new Date(name, required);
	}

	public static Date filter() {
		String name = "";
		return new Date(name, false);
	}
	
	private Date(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "date";
		this.required = required;
	}

	public Date addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Date setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Date setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Date setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		set.putAll(params);
		if (value != null) {
			set.put("value", value);
		}
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = getFilterSettings();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		if (required) {
			json.put("required", required);
		}
		if (disabled) {
			json.put("disabled", disabled);
		}
		if (title != null) {
			json.put("title", title);
		}
		return json;
	}

}
