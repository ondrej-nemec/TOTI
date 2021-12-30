package toti.control.inputs;

import java.util.Map;

public class Hidden implements Input {
	
	private final Wrapper wrapper;
	
	public static Hidden input(String name) {
		return new Hidden(name);
	}
	
	private Hidden(String name) {
		this.wrapper = new Wrapper("hidden", name, false);
	}

	public Hidden addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
/*
	public Hidden setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
*/
	public Hidden setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
/*
	public Hidden setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public Hidden setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Hidden setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}

	public Hidden setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		return json;
	}

}
