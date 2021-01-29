package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.columns.Filter;

public class Text implements Input, Filter {
	
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
	private String placeholder = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Text input(String name, boolean required) {
		return new Text(name, required);
	}
	
	public static Text filter() {
		String name = "";
		return new Text(name, false);
	}
	
	private Text(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "text";
		this.required = required;
	}

	public Text addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Text setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}
	
	public Text setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Text setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public Text setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}
	
	public Text setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Text setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public Text setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		if (size != null) {
			set.put("size", size);
		}
		if (maxLength != null) {
			set.put("maxlength", maxLength);
		}
		if (minLength != null) {
			set.put("minlength", minLength);
		}
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
		if (title != null) {
			json.put("title", title);
		}
	/*	if (size != null) {
			json.put("size", size);
		}
		if (maxLength != null) {
			json.put("maxlength", maxLength);
		}
		if (minLength != null) {
			json.put("minlength", minLength);
		}*/
		if (value != null) {
			json.put("value", value);
		}
		if (placeholder != null) {
			json.put("placeholder", placeholder);
		}
		params.forEach((key, param)->{
			json.put(key, param);
		});
		return json;
	}

}
