package toti.control.inputs;

import java.util.Map;

public class Password implements Input {
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	private boolean isOptional = false;
	
	private final Wrapper wrapper;
	
	public static Password input(String name, boolean required) {
		return new Password(name, required);
	}
	
	private Password(String name, boolean required) {
		this.wrapper = new Wrapper("password", name, required);
	}
	
	public Password setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Password setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public Password setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}
	
	public Password setOptional(boolean isOptional) {
		this.isOptional = isOptional;
		return this;
	}
	
	/*************/

	public Password addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Password setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Password setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Password setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public Password setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
	
	public Password setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Password setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		json.put("optional", isOptional);
		if (size != null) {
			json.put("size", size);
		}
		if (maxLength != null) {
			json.put("maxlength", maxLength);
		}
		if (minLength != null) {
			json.put("minlength", minLength);
		}
		return json;
	}

}
