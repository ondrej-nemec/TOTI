package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;

public class Radio implements Jsonable, Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private String value = null;
	
	public Radio(String name, String id, boolean required) {
		this.name = name;
		this.id = id;
		this.type = "radio";
		this.required = required;
	}
	
	public Radio setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Radio setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		if (title != null) {
			json.put("title", title);
		}
		json.put("required", required);
		if (value != null) {
			json.put("value", value);
		}
		return toJson(json);
	}

}
