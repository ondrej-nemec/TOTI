package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Range implements Input, Filter {
	
	private Integer step = null;
	private Integer min = null;
	private Integer max = null;
	
	private final Wrapper wrapper;
	
	public static Range filter() {
		String name = "";
		return new Range(name, false);
	}
	
	public static Range input(String name, boolean required) {
		return new Range(name, required);
	}
	
	private Range(String name, boolean required) {
		this.wrapper = new Wrapper("range", name, required);
	}
	
	public Range setStep(Integer step) {
		this.step = step;
		return this;
	}

	public Range setMin(Integer min) {
		this.min = min;
		return this;
	}

	public Range setMax(Integer max) {
		this.max = max;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Range addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Range setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Range setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Range setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Range setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Range setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Range setEditable(boolean editable) {
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
