package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Color implements Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Color input(String name, boolean required) {
		return new Color(name, required);
	}
	
	private Color(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "color";
		this.required = required;
	}

	public Color addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Color setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Color setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Color setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
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
