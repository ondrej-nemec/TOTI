package mvc.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.control.columns.Filter;
import mvc.templating.Template;

public class Select implements Jsonable, Input, Filter {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private final Map<String, String> options;
	private String value = null;
	
	public Select(String name,  String id, boolean required, Map<String, String> options) {
		this.name = name;
		this.id = id;
		this.type = "select";
		this.required = required;
		this.options = options;
	}
	
	public Select setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Select setDefaultValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = new HashMap<>();
		List<Map<String, Object>> opt = new LinkedList<>();
		options.forEach((value, text)->{
			Map<String, Object> param = new HashMap<>();
			param.put("value", value);
			param.put("text", Template.escapeVariable(text));
			opt.add(param);
		});
		set.put("options", opt);
		return set;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>(getFilterSettings());
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		json.put("required", required);
		if (title != null) {
			json.put("title", title);
		}
	/*	List<Map<String, Object>> opt = new LinkedList<>();
		options.forEach((value, text)->{
			Map<String, Object> param = new HashMap<>();
			param.put("value", value);
			param.put("text", Template.escapeVariable(text));
			opt.add(param);
		});
		json.put("options", opt);*/
		if (value != null) {
			json.put("value", value);
		}
		return toJson(json);
	}
	
}
