package toti.lib.database.querybuilder.builders.parents;

import java.util.function.Function;

import toti.lib.database.querybuilder.Functions;

public interface Ordered<P> {

	default P orderBy(String orderBy) {
		return orderBy(f->orderBy);
	}
	
	P orderBy(Function<Functions, String> orderBy);

}
