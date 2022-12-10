package toti.tutorial1.services;

import java.sql.SQLException;

import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.enums.Join;
import toti.application.GridEntityDao;
import toti.tutorial1.entity.State;

public class DeviceStateDao implements GridEntityDao<State> {

	public static final String TABLE = "states";
	
	private final Database database;
	
	public DeviceStateDao(Database database) {
		this.database = database;
	}

	@Override
	public SelectBuilder _getGrid(String select, QueryBuilder builder) {
		return builder.select(select)
			.from(
				builder.select("state.*, device.name device_name")
					.from(TABLE, "state")
					.join(DevicesDao.TABLE, "device", Join.INNER_JOIN, "device.id = state.device_id"),
				"data"
			);
	}
	
	public void saveState(int deviceId, State state) throws SQLException {
		database.applyBuilder((builder)->{
			if (builder.get(builder, TABLE, "device_id", deviceId) == null) { // insert
				builder.insert(builder, TABLE, state.toMap());
			} else { // update
				builder.update(builder, TABLE, "device_id", deviceId, state.toMap());
			}
			return null;
		});
	}
	
	public void deleteState(int deviceId) throws SQLException {
		database.applyBuilder((builder)->{
			builder.delete(TABLE).where("device_id = :deviceId").addParameter(":deviceId", deviceId).execute();
			return null;
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
	
}
