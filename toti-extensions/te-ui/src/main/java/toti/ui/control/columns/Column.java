package toti.ui.control.columns;

import java.util.Map;

import toti.lib.files.json.Jsonable;

public interface Column extends Jsonable {
	
	Map<String, Object> getGridSettings();
	
	@Override
	default Object toJson() {
		return getGridSettings();
	}

}
