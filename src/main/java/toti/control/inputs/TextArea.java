package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class TextArea implements Input {

	private final String name;
	private final String id;
	private final String type;
	private String title;	
	private final boolean required;
	private int cols;
	private int rows;
	private boolean disabled = false;
	private String value = null;
	private final Map<String, String> params = new HashMap<>();
	
	public static TextArea input(String name, boolean required) {
		return new TextArea(name, required);
	}
	
	private TextArea(String name, boolean required) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "textarea";
		this.required = required;
		this.cols = 10;
		this.rows = 10;
	}

	public TextArea addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	public TextArea setTitle(String title) {
		this.title = title;
		return this;
	}

	public TextArea setCols(int cols) {
		this.cols = cols;
		return this;
	}

	public TextArea setRows(int rows) {
		this.rows = rows;
		return this;
	}

	public TextArea setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public TextArea setValue(String value) {
		this.value = value;
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
		json.put("cols", cols);
		json.put("rows", rows);
		params.forEach((key, param)->{
			json.put(key, param);
		});
		if (title != null) {
			json.put("title", title);
		}
		if (value != null) {
			json.put("value", value);
		}
		return json;
	}
	
}
