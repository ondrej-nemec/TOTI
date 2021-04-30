package toti.control.inputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toti.control.columns.Filter;

public class Select implements Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private Boolean exclude = null;
	private Boolean editable = null;
	private final List<Option> options;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	private Map<String, Object> load;
	private String optionGroup;
	private String depends = null;

	public static Select input(String name, boolean required, List<Option> options) {
		return new Select(name, required, options);
	}
	
	public static Select filter(List<Option> options) {
		return new Select("", false, options);
	}
	
	private Select(String name, boolean required, List<Option> options) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "select";
		this.required = required;
		this.options = options;
	}

	public Select addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Select setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Select setDefaultValue(Object value) {
		if (value != null) {
			this.value = value.toString();
		} else {
			this.value = null;
		}
		return this;
	}
	
	public Select setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (exclude == null) {
			exclude = disabled;
		}
		return this;
	}
	
	public Select setShowedOptionGroup(String optionGroup) {
		this.optionGroup = optionGroup;
		return this;
	}

	public Select setLoadData(String url, String method) {
		return setLoadData(url, method, new HashMap<>());
	}
	
	public Select setLoadData(String url, String method, Map<String, String> params) {
		this.load = new HashMap<>();
		load.put("url", url);
		load.put("method", method);
		load.put("params", params);
		return this;
	}
	
	public Select setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
	
	public Select setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		if (optionGroup != null) {
			set.put("optionGroup", optionGroup);
		}
		set.put("options", options);
		if (load != null) {
			set.put("load", load);
		}
		set.putAll(params);
		if (value != null) {
			set.put("value", value);
		}
		if (depends != null) {
			set.put("depends", depends);
		}
		return set;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = getFilterSettings();
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
		if (title != null) {
			json.put("title", title);
		}
		return json;
	}
	
}
