package toti.control.columns;

import java.util.Map;

import json.Jsonable;

public interface Column extends Jsonable {
	
	Map<String, Object> getGridSettings();
	
	@Override
	default Object toJson() {
		return getGridSettings();
	}

}
