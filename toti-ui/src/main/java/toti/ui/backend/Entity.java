package toti.ui.backend;

import java.util.Map;

import ji.common.functions.Mapper;
import ji.json.Jsonable;

public interface Entity extends Jsonable {
	
	static final String SERIALIZE_DATABASE = "database";
	static final String SERIALIZE_JSON = "json";

	default Map<String, Object> toMap() {
		return serialize(SERIALIZE_DATABASE);
	}
	
	@Override
	default Object toJson() {
		return serialize("");
	}
	
	default Map<String, Object> serialize(String key) {
		return Mapper.get().serialize(this, key);
	}
	
}
