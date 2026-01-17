package toti.lib.database.querybuilder.builders.parents;

import java.util.function.Function;

import toti.lib.database.querybuilder.Functions;
import toti.lib.database.querybuilder.builders.MultipleSelectBuilder;
import toti.lib.database.querybuilder.builders.SelectBuilder;
import toti.lib.database.querybuilder.structures.StringWrapper;
import toti.lib.database.querybuilder.structures.SubSelect;

public interface PlainSelect<P> extends Wheres<P>, Parametrized<P>, Joins<P>, Ordered<P> {
	
	default P select(String select) {
		return select(f->select);
	}
	
	P select(Function<Functions, String> select);

	default P from(String table) {
		return _from(new StringWrapper(table), null);
	}

	default P from(String table, String alias) {
		return _from(new StringWrapper(table), alias);
	}
	
	default P from(SelectBuilder builder, String alias) {
		return _from(builder, alias);
	}
	
	default P from(MultipleSelectBuilder builder, String alias) {
		return _from(builder, alias);
	}
	
	P _from(SubSelect select, String alias);
	
	P groupBy(String groupBy);
	
	default P having(String having) {
		return having(n->having);
	}
	
	P having(Function<Functions, String> having);
	
	P limit(int limit);
	
	P limit(int limit, int offset);
	
}
