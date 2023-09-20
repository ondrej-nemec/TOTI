package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Time implements Input, Filter {
	
	private int step = 1;
	
	private final Wrapper wrapper;
	
	public static Time input(String name, boolean required) {
		return new Time(name, required);
	}

	public static Time filter() {
		String name = "";
		return new Time(name, false);
	}
	
	private Time(String name, boolean required) {
		this.wrapper = new Wrapper("time", name, required);
	}
	
	public Time setStep(int step) {
		this.step = step;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Time addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Time setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Time setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Time setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Time setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Time setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Time setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		set.put("step", step);
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}

}
