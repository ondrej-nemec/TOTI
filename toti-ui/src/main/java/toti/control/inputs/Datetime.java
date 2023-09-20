package toti.control.inputs;

import java.util.Map;

import toti.control.columns.Filter;

public class Datetime implements Input, Filter {
	
	private int step = 1;
	private boolean strict = true;
	
	private final Wrapper wrapper;
	
	public static Datetime input(String name, boolean required) {
		return new Datetime(name, required, true);
	}

	public static Datetime filter() {
		String name = "";
		return new Datetime(name, false, false);
	}
	
	private Datetime(String name, boolean required, boolean strict) {
		this.wrapper = new Wrapper("datetime-local", name, required);
		this.strict = strict;
	}
	
	public Datetime setStrict(boolean strict) {
		this.strict = strict;
		return this;
	}
	
	public Datetime setStep(int step) {
		this.step = step;
		return this;
	}

	@Override
	public String getType() {
		return wrapper.getType();
	}
	
	/*************/

	public Datetime addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Datetime setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Datetime setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Datetime setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Datetime setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Datetime setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Datetime setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}

	@Override
	public Map<String, Object> getFilterSettings() {
		Map<String, Object> set = wrapper.getFilterSettings();
		set.put("step", step);
		set.put("strict", strict);
		return set;
	}

	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings(getFilterSettings());
		return json;
	}

}
