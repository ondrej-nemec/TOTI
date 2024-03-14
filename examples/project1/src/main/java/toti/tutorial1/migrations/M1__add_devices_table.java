package toti.tutorial1.migrations;

import java.sql.SQLException;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import toti.tutorial1.services.DevicesDao;

public class M1__add_devices_table implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable(DevicesDao.TABLE)
		.addColumn("id", ColumnType.integer(), ColumnSetting.PRIMARY_KEY, ColumnSetting.AUTO_INCREMENT)
		.addColumn("name", ColumnType.string(50), ColumnSetting.NOT_NULL)
		.addColumn("ip", ColumnType.string(30), ColumnSetting.NOT_NULL)
		.addColumn("is_running", ColumnType.bool(), ColumnSetting.NOT_NULL)
		.execute();
	}

}
