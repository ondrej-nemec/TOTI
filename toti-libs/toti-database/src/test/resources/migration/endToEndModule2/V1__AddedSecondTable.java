package migration.endToEndModule2;

import java.sql.SQLException;

import toti.lib.database.migration.Migration;
import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.database.querybuilder.enums.ColumnType;

public class V1__AddedSecondTable implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable("Second_table").addColumn("id", ColumnType.integer()).execute();
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {
		builder.deleteTable("Second_table").execute();
	}

}
