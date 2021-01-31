package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.columns.Filter;

public class Month implements Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private String value = null;
	private int step = 1;
	private final Map<String, String> params = new HashMap<>();
	
	public static Month input(String name, boolean required) {
		return new Month(name, required);
	}

	public static Month filter() {
		String name = "";
		return new Month(name, false);
	}
	
	private Month(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "month";
		this.required = required;
	}

	public Month addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Month setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Month setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Month setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	public Month setStep(int step) {
		this.step = step;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		json.put("step", step);
		if (required) {
			json.put("required", required);
		}
		if (disabled) {
			json.put("disabled", disabled);
		}
		params.forEach((key, param)->{
			json.put(key, param);
		});
		if (title != null) {
			json.put("title", title);
		}
		if (value != null) {
			json.put("value", value);
		}
		return json;
	}

}
