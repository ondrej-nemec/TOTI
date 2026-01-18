package toti.lib.testing;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import toti.lib.database.base.Database;
import toti.lib.database.base.DatabaseConfig;
import toti.lib.testing.entities.Table;

public abstract class DatabaseTestCase {
	
	protected final DatabaseMock database;
	protected final DatabaseConfig config;

	public DatabaseTestCase(DatabaseConfig config) {
		this(config, Mockito.mock(Logger.class));
	}

	public DatabaseTestCase(DatabaseConfig config, Logger logger) {
		this.config = config;
		this.database = new DatabaseMock(config, getDataSet(), logger);
	}

	protected abstract List<Table> getDataSet();
	
	@BeforeEach
	public void before() throws SQLException {
		database.migrate();
		applyDataSet();
	}
	
	@AfterEach
	public void after() throws SQLException {
		database.rollback();
		database.close();
	}
	
	public Database getDatabase() {
		return database;
	}

	protected void alterDataSet(List<Table> tables) throws SQLException {
		database.applyDataSet(tables);
	}
	
	/**
	 * protected for test only
	 * using without REALLY good reason could make troubles
	 * @throws SQLException 
	 */
	protected void applyDataSet() throws SQLException {
		database.applyDataSet();
	}

}