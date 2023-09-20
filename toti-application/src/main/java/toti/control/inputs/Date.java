package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Date implements Input, Filter {
	
	private final Wrapper wrapper;
	
	public static Date input(String name, boolean required) {
		return new Date(name, required);
	}

	public static Date filter() {
		String name = "";
		return new Date(name, false);
	}
	
	private Date(String name, boolean required) {
		this.wrapper = new Wrapper("date", name, required);
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Date addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Date setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Date setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Date setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Date setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Date setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Date setEditable(boolean editable) {
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
