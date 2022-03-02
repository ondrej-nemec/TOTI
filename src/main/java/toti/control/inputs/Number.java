package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Number implements Input, Filter {
		
	private java.lang.Number step = null;
	private java.lang.Number min = null;
	private java.lang.Number max = null;
	
	private final Wrapper wrapper;
	
	public static Number filter() {
		String name = "";
		return new Number(name, false);
	}
	
	public static Number input(String name, boolean required) {
		return new Number(name, required);
	}
	
	private Number(String name, boolean required) {
		this.wrapper = new Wrapper("number", name, required);
	}
	
	public Number setStep(java.lang.Number step) {
		this.step = step;
		return this;
	}

	public Number setMin(java.lang.Number min) {
		this.min = min;
		return this;
	}

	public Number setMax(java.lang.Number max) {
		this.max = max;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Number addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Number setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Number setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Number setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}

	public Number setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
	
	public Number setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Number setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		if (step != null) {
			set.put("step", step);
		}
		if (max != null) {
			set.put("max", max);
		}
		if (min != null) {
			set.put("min", min);
		}
		return set;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}

}
