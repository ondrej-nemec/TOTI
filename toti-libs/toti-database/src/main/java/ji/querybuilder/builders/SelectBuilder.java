package ji.querybuilder.builders;


import ji.querybuilder.Builder;
import ji.querybuilder.builders.parents.Fetch;
import ji.querybuilder.builders.parents.PlainSelect;
import ji.querybuilder.structures.SubSelect;

public interface SelectBuilder extends Builder, SubSelect, PlainSelect<SelectBuilder>, Fetch {

	@Override
	default String createSql() {
		return Builder.super.createSql();
	}

}
