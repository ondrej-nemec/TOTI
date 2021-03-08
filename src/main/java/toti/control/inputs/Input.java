package toti.control.inputs;

import java.util.Map;

import json.Jsonable;

public interface Input extends Jsonable {

	Map<String, Object> getInputSettings();
	
	@Override
	default Object toJson() {
		return getInputSettings();
	}
	
}
