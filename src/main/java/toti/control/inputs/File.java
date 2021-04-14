package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class File implements Input {

	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private boolean disabled = false;
	private Boolean exclude = null; // TODO setry
	private boolean editable = false; // TODO setry
	private final Map<String, String> params = new HashMap<>();

	public static File input(String name, boolean required) {
		return new File(name, required);
	}
	
	private File(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "file";
		this.required = required;
	}

	public File addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public File setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public File setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (exclude == null) {
			exclude = disabled;
		}
		return this;
	}
	
	public File setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
	
	public File setEditable(boolean editable) {
		this.editable = editable;
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
		if (exclude != null && exclude) {
			json.put("exclude", exclude);
		}
		if (editable) {
			json.put("editable", editable);
		}
		if (title != null) {
			json.put("title", title);
		}
		params.forEach((key, param)->{
			json.put(key, param);
		});
		return json;
	}

}
