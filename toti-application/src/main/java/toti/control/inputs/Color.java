package toti.control.inputs;

import java.util.Map;

public class Color implements Input {
	
	private final Wrapper wrapper;
	
	public static Color input(String name, boolean required) {
		return new Color(name, required);
	}
	
	private Color(String name, boolean required) {
		this.wrapper = new Wrapper("color", name, required);
	}

	public Color addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Color setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Color setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Color setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Color setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Color setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Color setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		return json;
	}

}
