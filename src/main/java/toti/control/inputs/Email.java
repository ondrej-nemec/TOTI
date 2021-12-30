package toti.control.inputs;

import java.util.Map;

public class Email implements Input {
	
	private final Wrapper wrapper;
	
	public static Email input(String name, boolean required) {
		return new Email(name, required);
	}
	
	private Email(String name, boolean required) {
		this.wrapper = new Wrapper("email", name, required);
	}

	public Email addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Email setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Email setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Email setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public Email setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
	
	public Email setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Email setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		return json;
	}

}
