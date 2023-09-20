package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Month implements Input, Filter {
	
	private final Wrapper wrapper;
	
	public static Month input(String name, boolean required) {
		return new Month(name, required);
	}

	public static Month filter() {
		String name = "";
		return new Month(name, false);
	}
	
	private Month(String name, boolean required) {
		this.wrapper = new Wrapper("month", name, required);
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Month addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Month setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Month setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Month setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Month setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Month setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Month setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}

}
