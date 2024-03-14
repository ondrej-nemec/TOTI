package toti.ui.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RadioList implements Input {
	
	private final List<Map<String, String>> radios;
	
	private final Wrapper wrapper;
	
	public static RadioList input(String name, boolean required, Map<String, String> radios) {
		return new RadioList(name, required, radios);
	}
	
	private RadioList(String name, boolean required, Map<String, String> radios) {
		this.radios = new LinkedList<>();
		radios.forEach((value, title)->{
			Map<String, String> radio = new HashMap<>();
			radio.put("value", value);
			radio.put("title", title);
			this.radios.add(radio);
		});
		this.wrapper = new Wrapper("radiolist", name, required);
	}
	
	/*************/

	public RadioList addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public RadioList setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public RadioList setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public RadioList setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public RadioList setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public RadioList setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public RadioList setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		json.put("radios", radios);
		return json;
	}

}
