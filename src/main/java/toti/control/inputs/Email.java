package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Email implements Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private Boolean exclude = null;
	private Boolean editable = null;
	private String value = null;
	private String placeholder = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Email input(String name, boolean required) {
		return new Email(name, required);
	}
	
	private Email(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "email";
		this.required = required;
	}

	public Email addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Email setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Email setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Email setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (exclude == null) {
			exclude = disabled;
		}
		return this;
	}
	
	public Email setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
	
	public Email setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	public Email setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
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
		if (exclude != null) {
			json.put("exclude", exclude);
		}
		if (editable != null) {
			json.put("editable", editable);
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
		if (placeholder != null) {
			json.put("placeholder", placeholder);
		}
		return json;
	}

}
