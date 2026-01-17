package toti.lib.testing;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.Logger;

import toti.lib.database.base.support.ConnectionFunction;
import toti.lib.database.base.support.DoubleConsumer;
import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.QueryBuilderFactory;
import toti.lib.database.querybuilder.builders.BatchBuilder;
import toti.lib.database.querybuilder.builders.InsertBuilder;
import toti.lib.database.querybuilder.builders.UpdateBuilder;
import toti.lib.testing.entities.Row;
import toti.lib.testing.entities.Table;
import toti.lib.database.base.Database;
import toti.lib.database.base.DatabaseConfig;

public class DatabaseMock extends Database {
	
	private final List<Table> tables;
	
	private final Connection connection;
	
	public DatabaseMock(DatabaseConfig config, final List<Table> tables, Logger logger) {
		super(config, true, null, logger); // profiler is null
		this.tables = tables;
		try {
			createDbIfNotExists();
			this.connection = pool.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Connection to database could not be created", e);
		}
		
	}

	public void applyDataSet() throws SQLException {
		applyDataSet(tables);
	}

	protected void applyDataSet(List<Table> tables) throws SQLException {
		applyBuilder((builder)->{
			BatchBuilder batch = builder.batch();
			for(Table table : tables) {
				for(Row row : table.getRows()) {
					batch.addBatch(applyRow(builder, batch, table.getName(), row));
				}
			}
			batch.execute();
			return null;
		});
	}
	
	private Builder applyRow(QueryBuilderFactory builder, BatchBuilder batch, String table, Row row) {
		if (row.isInsert()) {
			InsertBuilder insert = builder.insert(table, row.getIdName());
			row.getColumns().forEach((key, value)->{
				insert.addValue(key, value);
			});
			return insert;
		}
		UpdateBuilder update = builder.update(table);
		row.getColumns().forEach((key, value)->{
			update.set(key + " = :" + key)
			.addParameter(":" + key, value);
		});
		String idName = row.getIdName().get();
		update.where(idName + " = :" + idName).addParameter(":" + idName, row.getIdValue());
		return update;
	}
	
	@Override
	protected <T> DoubleConsumer<T> getDoubleFunction(ConnectionFunction<T> consumer) {
		return ()->{
			return consumer.apply(connection);
		};
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}
	
	protected void rollback() throws SQLException {
		connection.rollback();
		//pool.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		pool.close();
	}
}
