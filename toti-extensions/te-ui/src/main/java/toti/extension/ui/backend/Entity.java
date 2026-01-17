package toti.extension.ui.backend;

import java.util.Map;

import toti.lib.common.functions.Mapper;
import toti.lib.files.json.Jsonable;

public interface Entity extends Jsonable {
	
	static final String SERIALIZE_DATABASE = "database";
	static final String SERIALIZE_JSON = "json";

	default Map<String, Object> toMap() {
		return serialize(SERIALIZE_DATABASE);
	}
	
	@Override
	default Object toJson() {
		return serialize(SERIALIZE_JSON);
	}
	
	default Map<String, Object> serialize(String key) {
		return Mapper.get().serialize(this, key);
	}
	
}
