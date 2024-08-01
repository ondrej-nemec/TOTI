package ji.querybuilder.builders.parents;

import java.util.function.Function;

import ji.querybuilder.Functions;
import ji.querybuilder.builders.MultipleSelectBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.structures.StringWrapper;
import ji.querybuilder.structures.SubSelect;

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
	
	P limit(int limit, int offset);
	
}
