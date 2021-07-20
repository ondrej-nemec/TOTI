package toti.application;

import java.util.Map;

import common.functions.Mapper;
import json.Jsonable;

public interface Entity extends Jsonable {

	default Map<String, Object> toMap() {
		return Mapper.get().serialize(this, "database");
	}
	
	@Override
	default Object toJson() {
		return Mapper.get().serialize(this, "json");
	}
	
}
