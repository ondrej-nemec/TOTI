//package database;

import java.sql.SQLException;

import migration.Migration;
import querybuilder.ColumnType;
import querybuilder.QueryBuilder;

public class Init__Table implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder
			.createTable("query_test")
			.addColumn("id", ColumnType.integer())
			.addColumn("name", ColumnType.string(11))
			.addColumn("description", ColumnType.string(11))
			.addColumn("is_active", ColumnType.bool())
			.execute();

		builder
			.insert("query_test")
			.addValue("id", 10)
			.addValue("name", "Name #")
			.addValue("description", "Desc #")
			.addValue("is_active", false)
			.execute();
		
		for (int i = 0; i < 100; i++) {
			builder
				.insert("query_test")
				.addValue("id", 100 + i)
				.addValue("name", "Name #" + (i%3))
				.addValue("description", "Desc #" + (i%5))
				.addValue("is_active", i%2 == 0)
				.execute();
		}
		
	}

	@Override public void revert(QueryBuilder builder) throws SQLException {}

}
