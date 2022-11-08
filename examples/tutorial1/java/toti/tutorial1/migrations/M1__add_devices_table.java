package toti.tutorial1.migrations;

import java.sql.SQLException;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;
import toti.tutorial1.services.DevicesDao;

public class M1__add_devices_table implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable(DevicesDao.TABLE)
		
		.execute();
	}

}
