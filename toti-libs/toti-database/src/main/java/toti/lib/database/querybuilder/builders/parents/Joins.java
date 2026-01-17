package toti.lib.database.querybuilder.builders.parents;

import java.util.function.Function;

import toti.lib.database.querybuilder.Functions;
import toti.lib.database.querybuilder.builders.SelectBuilder;
import toti.lib.database.querybuilder.enums.Join;
import toti.lib.database.querybuilder.structures.StringWrapper;
import toti.lib.database.querybuilder.structures.SubSelect;

public interface Joins<P> {

	default P join(String table, Join join, String on) {
		return _join(new StringWrapper(table), null, join, f->on);
	}
	
	default P join(String table, String alias, Join join, String on) {
		return _join(new StringWrapper(table), alias, join, f->on);
	}

	default P join(SelectBuilder builder, String alias, Join join, String on) {
		return _join(builder, alias, join, f->on);
	}

	default P join(String table, Join join, Function<Functions, String> on) {
		return _join(new StringWrapper(table), null, join, on);
	}
	
	default P join(String table, String alias, Join join, Function<Functions, String> on) {
		return _join(new StringWrapper(table), alias, join, on);
	}

	default P join(SelectBuilder builder, String alias, Join join, Function<Functions, String> on) {
		return _join(builder, alias, join, on);
	}

	P _join(SubSelect builder, String alias, Join join, Function<Functions, String> on);
	
}
