package ji.querybuilder;

import java.util.Arrays;
import java.util.List;

import toti.lib.common.functions.Implode;

public interface Builder {

	String getSql();
	
	default List<String> getSqls() {
		return Arrays.asList(getSql());
	}

	default String createSql() {
		return getSql();
	}

	default List<String> createSqls() {
		return Arrays.asList(createSql());
	}
	
	default String _toString(List<String> queries) {
		return Implode.implode(";", queries);
	}

}
