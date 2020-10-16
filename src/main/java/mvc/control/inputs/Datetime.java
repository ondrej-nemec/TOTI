package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.control.columns.Filter;

public class Datetime implements Jsonable, Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private String value = null;
	
	public Datetime(String name, String id, boolean required) {
		this.name = name;
		this.id = id;
		this.type = "datetime";
		this.required = required;
	}
	
	public Datetime setTitle(String title) {
		this.title = title;
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
	
	public Datetime setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		json.put("required", required);
		if (title != null) {
			json.put("title", title);
		}
		if (value != null) {
			json.put("value", value);
		}
		return toJson(json);
	}

}
