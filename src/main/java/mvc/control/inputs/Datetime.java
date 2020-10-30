package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.columns.Filter;

public class Datetime implements Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Datetime input(String name, boolean required) {
		return new Datetime(name, required);
	}

	public static Datetime filter() {
		String name = "";
		return new Datetime(name, false);
	}
	
	private Datetime(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "datetime";
		this.required = required;
	}

	public Datetime addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Datetime setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Datetime setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Datetime setDisabled(boolean disabled) {
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
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
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
