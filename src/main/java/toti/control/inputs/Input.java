package toti.control.inputs;

import java.util.Map;

import ji.json.Jsonable;

public interface Input extends Jsonable {

	Map<String, Object> getInputSettings();
	
	@Override
	default Object toJson() {
		return getInputSettings();
	}
	/*
	I addParam(String name, String value);
	
	I setDefaultValue(Object value);
	
	I setPlaceholder(String placeholder);
	
	I setTitle(String title);
	
	I setDisabled(boolean disabled);
	
	I setExclude(boolean exclude);
	
	I setEditable(boolean editable);
	*/
}
