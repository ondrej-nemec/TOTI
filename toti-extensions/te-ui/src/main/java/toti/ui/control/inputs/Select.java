package toti.ui.control.inputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toti.ui.control.columns.Filter;

public class Select implements Input, Filter {
	
	private final List<Option> options;
	private Map<String, Object> load;
	private String optionGroup;
	private String depends = null;
	private boolean selfReference = false;
	private boolean search = false;
	private String prompt = null;
	
	private final Wrapper wrapper;

	public static Select input(String name, boolean required, List<Option> options) {
		return new Select(name, required, options);
	}
	
	public static Select filter(List<Option> options) {
		return new Select("", false, options);
	}
	
	private Select(String name, boolean required, List<Option> options) {
		this.wrapper = new Wrapper("select", name, required);
		this.options = options;
	}
	
	public Select setDepends(String depends) {
        this.depends = depends;
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

	public Select setSearch(boolean search) {
		this.search = search;
		return this;
	}
	
	public Select setPrompt(String prompt) {
		this.prompt = prompt;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Select addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Select setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Select setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Select setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
	
	public Select setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Select setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	public Select setSelfReference(boolean selfReference) {
		this.selfReference = selfReference;
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		if (optionGroup != null) {
			set.put("optionGroup", optionGroup);
		}
		set.put("options", options);
		if (load != null) {
			set.put("load", load);
		}
		if (depends != null) {
			set.put("depends", depends);
		}
		set.put("selfReference", selfReference);
		set.put("search", search);
		if (search) {
			set.put("autocomplete", "off");
		}
		if (prompt != null) {
			set.put("prompt", prompt);
		}
		return set;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}
	
}
