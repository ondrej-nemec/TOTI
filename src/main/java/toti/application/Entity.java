package toti.application;

import java.util.Map;

import common.functions.Mapper;

public interface Entity {

	default Map<String, Object> toMap() {
		return Mapper.get().serialize(this);
	}
	
	
}
