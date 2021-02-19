package example.dao.migrations;

import java.sql.SQLException;

import migration.Migration;
import querybuilder.ColumnSetting;
import querybuilder.ColumnType;
import querybuilder.QueryBuilder;

public class E1__example_table implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable("example")
			.addColumn("id", ColumnType.integer(), ColumnSetting.AUTO_INCREMENT, ColumnSetting.PRIMARY_KEY)
			.addColumn("name", ColumnType.string(100))
			.addColumn("email", ColumnType.string(100))
			.addColumn("age", ColumnType.integer())
			.addColumn("pasw", ColumnType.string(100))
			.addColumn("range", ColumnType.integer())
			.addColumn("active", ColumnType.bool())
			.addColumn("defValue", ColumnType.bool())
			.addColumn("sex", ColumnType.string(6))
			.addColumn("parent", ColumnType.integer())

			.addColumn("simple_date", ColumnType.string(20))
			.addColumn("dt_local", ColumnType.string(20))
			.addColumn("month", ColumnType.string(20))
			.addColumn("time", ColumnType.string(20))
			.addColumn("week", ColumnType.string(20))
			// TODO solve
			/*
			.addColumn("simple_date", ColumnType.datetime())
			.addColumn("dt_local", ColumnType.datetime())
			.addColumn("month", ColumnType.datetime())
			.addColumn("time", ColumnType.datetime())
			.addColumn("week", ColumnType.datetime())
			*/
			.addColumn("favorite_color", ColumnType.string(7))
			.addColumn("comment", ColumnType.string(255))
			.execute();
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {}

}
