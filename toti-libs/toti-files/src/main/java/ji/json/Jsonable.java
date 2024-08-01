package ji.json;

import ji.common.functions.Mapper;

public interface Jsonable {

	default Object toJson() {
		return Mapper.get().serialize(this);
	}
	
}
