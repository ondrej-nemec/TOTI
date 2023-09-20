package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Text implements Input, Filter {
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	
	private final Wrapper wrapper;
	
	public static Text input(String name, boolean required) {
		return new Text(name, required);
	}
	
	public static Text filter() {
		String name = "";
		return new Text(name, false);
	}
	
	private Text(String name, boolean required) {
		this.wrapper = new Wrapper("text", name, required);
	}
	
	public Text setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Text setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public Text setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Text addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Text setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Text setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Text setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public Text setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
	
	public Text setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Text setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		if (size != null) {
			set.put("size", size);
		}
		if (maxLength != null) {
			set.put("maxlength", maxLength);
		}
		if (minLength != null) {
			set.put("minlength", minLength);
		}
		return set;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}

}
