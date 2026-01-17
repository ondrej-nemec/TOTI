package toti.lib.database.querybuilder.builders;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.Fetch;
import toti.lib.database.querybuilder.builders.parents.Ordered;
import toti.lib.database.querybuilder.builders.parents.Parametrized;
import toti.lib.database.querybuilder.structures.SubSelect;

public interface MultipleSelectBuilder extends Builder, SubSelect, Fetch, Parametrized<MultipleSelectBuilder>, Ordered<MultipleSelectBuilder> {
	
	MultipleSelectBuilder union(SelectBuilder select);
	
	MultipleSelectBuilder intersect(SelectBuilder select);
	
	MultipleSelectBuilder unionAll(SelectBuilder select);
	
	MultipleSelectBuilder except(SelectBuilder select);

	@Override
	default String createSql() {
		return Builder.super.createSql();
	}
	
}
