package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.PlainSelect;

public interface CreateViewBuilder extends Builder, PlainSelect<CreateViewBuilder> {

	int execute() throws SQLException;

}
