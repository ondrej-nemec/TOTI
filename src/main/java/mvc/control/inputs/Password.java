package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Password implements Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Password input(String name, boolean required) {
		return new Password(name, required);
	}
	
	private Password(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "password";
		this.required = required;
	}

	public Password addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Password setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Password setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Password setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public Password setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}
	
	public Password setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Password setDisabled(boolean disabled) {
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
		if (size != null) {
			json.put("size", size);
		}
		if (maxLength != null) {
			json.put("maxlength", maxLength);
		}
		if (minLength != null) {
			json.put("minlength", minLength);
		}
		if (value != null) {
			json.put("value", value);
		}
		return json;
	}

}
