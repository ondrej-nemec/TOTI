package toti.application;

import java.util.Map;

import common.functions.Mapper;
import json.Jsonable;

public interface Entity extends Jsonable {

	default Map<String, Object> toMap() {
		return serialize("database");
	}
	
	@Override
	default Object toJson() {
		return serialize("json");
	}
	
	default Map<String, Object> serialize(String key) {
		return Mapper.get().serialize(this, key);
	}
	
}
