package toti.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
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
	private final List<Option> options;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	private Map<String, Object> load;
	private String optionGroup;
	
	@Deprecated
	public static Select input(String name, boolean required, Map<String, String> options) {
		List<Option> opts = new LinkedList<>();
		options.forEach((value, title)->{
			opts.add(Option.input(value, title));
		});
		return new Select(name, required, opts);
	}

	@Deprecated
	public static Select filter(Map<String, String> options) {
		return Select.input("", false, options);
	}
	
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
	
	public Select setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Select setDisabled(boolean disabled) {
		this.disabled = disabled;
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

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		/*Map<String, Object> opt = new HashMap<>();
		options.forEach((value, text)->{
			Map<String, Object> param = new HashMap<>();
			param.put("value", value);
			param.put("title", Template.escapeVariable(text));
			
			// disabled, groupname
			opt.put(value, param);
		});*/
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
		if (title != null) {
			json.put("title", title);
		}
		return json;
	}
	
}
