package toti.control.inputs;

import java.util.Map;

public class TextArea implements Input {

	private int cols;
	private int rows;
	private Integer maxLength;
	
	private final Wrapper wrapper;
	
	public static TextArea input(String name, boolean required) {
		return new TextArea(name, required);
	}
	
	private TextArea(String name, boolean required) {
		this.wrapper = new Wrapper("textarea", name, required);
		this.cols = 10;
		this.rows = 10;
	}
	
	public TextArea setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
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
	
	/*************/

	public TextArea addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public TextArea setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public TextArea setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public TextArea setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public TextArea setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
	
	public TextArea setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public TextArea setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		json.put("cols", cols);
		json.put("rows", rows);
		if (maxLength != null) {
			json.put("maxlenght", maxLength);
		}
		return json;
	}
	
}
