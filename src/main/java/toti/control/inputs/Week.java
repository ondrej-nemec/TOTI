package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Week implements Input, Filter {

	private final Wrapper wrapper;
	
	public static Week input(String name, boolean required) {
		return new Week(name, required);
	}

	public static Week filter() {
		String name = "";
		return new Week(name, false);
	}
	
	private Week(String name, boolean required) {
		this.wrapper = new Wrapper("week", name, required);
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Week addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Week setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Week setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Week setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Week setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Week setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Week setEditable(boolean editable) {
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
