package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.PlainSelect;

public interface AlterViewBuilder extends Builder, PlainSelect<AlterViewBuilder> {
	
	int execute() throws SQLException;

}
