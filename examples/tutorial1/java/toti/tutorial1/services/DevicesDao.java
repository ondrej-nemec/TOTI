package toti.tutorial1.services;

import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import toti.tutorial1.entity.Device;
import toti.application.EntityDao;
import toti.application.GridEntityDao;

public class DevicesDao implements EntityDao<Device>, GridEntityDao<Device> {
	
	public static final String TABLE = "devices";
	
	private final Database database;
	
	public DevicesDao(Database database) {
		this.database = database;
	}

	@Override
	public SelectBuilder _getGrid(String select, QueryBuilder builder) {
		return builder._getAll(builder, getTableName(), select);
	}

	@Override
	public Database getDatabase() {
		return database;
	}

	@Override
	public String getTableName() {
		return TABLE;
	}

	@Override
	public Device createEntity(DatabaseRow row) {
		return row.parse(Device.class);
	}

}
