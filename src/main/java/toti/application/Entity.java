package toti.application;

import java.util.Map;

import json.Jsonable;

public interface Entity extends Jsonable {

	Map<String, Object> toMap();
	
	@Override
	default Object toJson() {
		return toMap();
	}
	
}
