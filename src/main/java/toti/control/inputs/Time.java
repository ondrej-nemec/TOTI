package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.columns.Filter;

public class Time implements Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private Boolean exclude = null; // TODO setry
	private boolean editable = false; // TODO setry
	private String value = null;
	private int step = 1;
	private final Map<String, String> params = new HashMap<>();
	
	public static Time input(String name, boolean required) {
		return new Time(name, required);
	}

	public static Time filter() {
		String name = "";
		return new Time(name, false);
	}
	
	private Time(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "time";
		this.required = required;
	}

	public Time addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Time setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Time setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Time setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (exclude == null) {
			exclude = disabled;
		}
		return this;
	}
	
	public Time setStep(int step) {
		this.step = step;
		return this;
	}
	
	public Time setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
	
	public Time setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		set.put("step", step);
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
		if (exclude != null && exclude) {
			json.put("exclude", exclude);
		}
		if (editable) {
			json.put("editable", editable);
		}
		if (title != null) {
			json.put("title", title);
		}
		return json;
	}

}
