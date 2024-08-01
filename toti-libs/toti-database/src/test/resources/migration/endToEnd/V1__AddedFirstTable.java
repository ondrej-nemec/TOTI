package migration.endToEnd;

import java.sql.SQLException;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.enums.ColumnType;

public class V1__AddedFirstTable implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable("First_table").addColumn("id", ColumnType.integer()).execute();
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {
		builder.deleteTable("First_table").execute();
	}

}
