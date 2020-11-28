package module.resources;

import java.sql.SQLException;

import migration.Migration;
import querybuilder.ColumnSetting;
import querybuilder.ColumnType;
import querybuilder.QueryBuilder;

public class M1__module_data implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable("entity")
			.addColumn("id", ColumnType.integer(), ColumnSetting.PRIMARY_KEY, ColumnSetting.AUTO_INCREMENT)
			.addColumn("name", ColumnType.string(50), ColumnSetting.NOT_NULL)
			.addColumn("secret", ColumnType.string(20), ColumnSetting.NOT_NULL)
			.addColumn("email", ColumnType.string(50), ColumnSetting.NULL)
			.addColumn("edited", ColumnType.datetime())
			.addColumn("is_main", ColumnType.bool(), ColumnSetting.NOT_NULL)
			.addColumn("rank", ColumnType.integer(), ColumnSetting.NOT_NULL)
			.addColumn("FK_id", ColumnType.integer(), ColumnSetting.NOT_NULL)
			.addColumn("lang", ColumnType.string(10), ColumnSetting.NOT_NULL)
			.execute();
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {}

}

/*
CREATE TABLE entity (
	id SERIAL,
	
	name varchar(50) not null,
	secret varchar(20) not null,
	edited datetime,
	email varchar(50) null,
	is_main boolean,
	-- file,
	rank int,
	FK_id int not null,
	lang varchar(10),
	PRIMARY KEY (id)
)
*/