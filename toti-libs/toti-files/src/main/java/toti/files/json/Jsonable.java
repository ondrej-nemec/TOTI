package toti.files.json;

import toti.common.functions.Mapper;

public interface Jsonable {

	default Object toJson() {
		return Mapper.get().serialize(this);
	}
	
}
