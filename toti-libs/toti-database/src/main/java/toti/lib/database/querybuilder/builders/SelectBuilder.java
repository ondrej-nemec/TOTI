package toti.lib.database.querybuilder.builders;


import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.Fetch;
import toti.lib.database.querybuilder.builders.parents.PlainSelect;
import toti.lib.database.querybuilder.structures.SubSelect;

public interface SelectBuilder extends Builder, SubSelect, PlainSelect<SelectBuilder>, Fetch {

	@Override
	default String createSql() {
		return Builder.super.createSql();
	}

}
