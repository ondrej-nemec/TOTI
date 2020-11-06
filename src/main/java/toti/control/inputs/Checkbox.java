package toti.control.inputs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Checkbox implements Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	private String checked = "Yes";
	private String unchecked = "No";
	
	public static Checkbox input(String name, boolean required) {
		return new Checkbox(name, required);
	}
	
	private Checkbox(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "checkbox";
		this.required = required;
	}
	
	public Checkbox setTitle(String title) {
		this.title = title;
		return this;
	}

	public Checkbox addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public Checkbox setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	public Checkbox setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	public Checkbox setValuesRender(String checked, String unchecked) {
		this.checked = checked;
		this.unchecked = unchecked;
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
		if (required) {
			json.put("required", required);
		}
		if (disabled) {
			json.put("disabled", disabled);
		}
		if (title != null) {
			json.put("title", title);
		}
		if (value != null) {
			json.put("value", value);
		}
		Map<String, Object> yes = new HashMap<>();
		yes.put("title", checked);
		yes.put("value", true);
		Map<String, Object> no = new HashMap<>();
		no.put("title", unchecked);
		no.put("value", false);
		json.put("values", Arrays.asList(yes, no));
		return json;
	}

}
