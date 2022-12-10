package toti.tutorial1.migrations;

import java.sql.SQLException;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import toti.tutorial1.services.DeviceStateDao;

public class M2__added_device_state_table implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable(DeviceStateDao.TABLE)
		.addColumn("id", ColumnType.integer(), ColumnSetting.PRIMARY_KEY, ColumnSetting.AUTO_INCREMENT)
		.addColumn("device_id", ColumnType.integer(), ColumnSetting.NOT_NULL)
		.addColumn("is_connected", ColumnType.bool(), ColumnSetting.NOT_NULL)
		.addColumn("detail", ColumnType.string(20), ColumnSetting.NOT_NULL)
		.execute();
	}

}
