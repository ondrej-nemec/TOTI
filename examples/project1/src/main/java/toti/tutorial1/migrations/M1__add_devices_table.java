package toti.tutorial1.migrations;

import java.sql.SQLException;

import toti.lib.database.migration.Migration;
import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
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
