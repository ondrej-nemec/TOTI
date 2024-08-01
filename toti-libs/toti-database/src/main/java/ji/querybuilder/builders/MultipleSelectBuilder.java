package ji.querybuilder.builders;

import ji.querybuilder.Builder;
import ji.querybuilder.builders.parents.Fetch;
import ji.querybuilder.builders.parents.Ordered;
import ji.querybuilder.builders.parents.Parametrized;
import ji.querybuilder.structures.SubSelect;

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
