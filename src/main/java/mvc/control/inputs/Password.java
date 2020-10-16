package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;

public class Password implements Jsonable, Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	private String value = null;
	
	public Password(String name, String id, boolean required) {
		this.name = name;
		this.id = id;
		this.type = "password";
		this.required = required;
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
		return toJson(json);
	}

}
