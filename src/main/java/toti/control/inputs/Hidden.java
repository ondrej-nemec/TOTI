package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Hidden implements Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String value = null;
	private Boolean exclude = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static Hidden input(String name) {
		return new Hidden(name);
	}
	
	private Hidden(String name) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "hidden";
	}

	public Hidden addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Hidden setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
	
	public Hidden setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		params.forEach((key, param)->{
			json.put(key, param);
		});
		if (value != null) {
			json.put("value", value);
		}
		if (exclude != null) {
			json.put("exclude", exclude);
		}
		return json;
	}

}
