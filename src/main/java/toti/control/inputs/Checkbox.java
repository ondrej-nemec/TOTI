package toti.control.inputs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Checkbox implements Input {
	
	private String checked = "Yes";
	private String unchecked = "No";
	private final Wrapper wrapper;
	
	public static Checkbox input(String name, boolean required) {
		return new Checkbox(name, required);
	}
	
	private Checkbox(String name, boolean required) {
		this.wrapper = new Wrapper("checkbox", name, required);
	}
	
	public Checkbox setValuesRender(String checked, String unchecked) {
		this.checked = checked;
		this.unchecked = unchecked;
		return this;
	}
	
	/*************/

	public Checkbox addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public Checkbox setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public Checkbox setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public Checkbox setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public Checkbox setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public Checkbox setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public Checkbox setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		
		Map<String, Object> yes = new HashMap<>();
		yes.put("title", checked);
		yes.put("value", true);
		
		Map<String, Object> no = new HashMap<>();
		no.put("title", unchecked);
		no.put("value", false);
		
		json.put("values", Arrays.asList(yes, no));
		return json;
	}

}
