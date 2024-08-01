package ji.querybuilder.builders.parents;

import java.util.function.Function;

import ji.querybuilder.Functions;
import ji.querybuilder.enums.Where;

public interface Wheres<P> {

	default P where(String where) {
		return where(n->where, Where.AND);
	}
	
	default P where(String where, Where join) {
		return where(n->where, join);
	}
	
	default P where(Function<Functions, String> where) {
		return where(where, Where.AND);
	}

	default P orWhere(String where) {
		return where(n->where, Where.OR);
	}
	
	default P orWhere(Function<Functions, String> where) {
		return where(where, Where.OR);
	}
	
	P where(Function<Functions, String> where, Where join);

}
