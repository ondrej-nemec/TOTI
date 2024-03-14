package toti.ui.control.inputs;

import java.util.Map;

public class File implements Input {

	private final Wrapper wrapper;

	public static File input(String name, boolean required) {
		return new File(name, required);
	}
	
	private File(String name, boolean required) {
		this.wrapper = new Wrapper("file", name, required);
	}

	public File addParam(String name, String value) {
		wrapper.addParam(name, value);
		return this;
	}
	
	public File setTitle(String title) {
		wrapper.setTitle(title);
		return this;
	}
	
	public File setDefaultValue(Object value) {
		wrapper.setDefaultValue(value);
		return this;
	}
	
	public File setDisabled(boolean disabled) {
		wrapper.setDisabled(disabled);
		return this;
	}
/*
	public File setPlaceholder(String placeholder) {
		wrapper.setPlaceholder(placeholder);
		return this;
	}
*/
	public File setExclude(boolean exclude) {
		wrapper.setExclude(exclude);
		return this;
	}
	
	public File setEditable(boolean editable) {
		wrapper.setEditable(editable);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = wrapper.getInputSettings();
		return json;
	}

}
