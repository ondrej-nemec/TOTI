package toti.tutorial1.services;

import java.sql.SQLException;
import java.util.List;

import ext.EntityDao;
import ext.GridEntityDao;
import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import toti.tutorial1.entity.Device;

public class DevicesDao implements EntityDao<Device>, GridEntityDao<Device> {
	
	public static final String TABLE = "devices";
	
	private final Database database;
	
	public DevicesDao(Database database) {
		this.database = database;
	}

	@Override
	public SelectBuilder _getGrid(String select, QueryBuilder builder) {
		return builder._getAll(getTableName(), select);
	}
	
	@Override
	public List<Device> getAll() throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.getAllBy(TABLE, "is_running", true, r->r.parse(Device.class));
		});
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
	
	@Override
	public String getHelpKey() {
		return "id";
	}
	
	@Override
	public String getHelpDisplayValue() {
		return "name";
	}

}
