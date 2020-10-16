package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.control.columns.Filter;

public class Text implements Jsonable, Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	private String value = null;
	
	public Text(String name, String id, boolean required) {
		this.name = name;
		this.id = id;
		this.type = "text";
		this.required = required;
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
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		json.put("required", required);
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
		return toJson(json);
	}

}
