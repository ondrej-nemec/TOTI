package ji.testing;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.testing.entities.Table;

public abstract class DatabaseTestCase {
	
	protected final DatabaseMock database;
	protected final DatabaseConfig config;

	public DatabaseTestCase(DatabaseConfig config) {
		this(config, Mockito.mock(Logger.class));
	}

	public DatabaseTestCase(DatabaseConfig config, Logger logger) {
		this.config = config;
		this.database = new DatabaseMock(
				config,
				getDataSet(),
				logger
		);
	}

	public DatabaseTestCase(Env env, Logger logger) {
		this(new DatabaseConfig(
				env.getString("database.type"),
				env.getString("database.url"),
				env.getString("database.schema-name"),
				env.getString("database.login"),
				env.getString("database.password"),
				env.getList("database.pathToMigrations", ","),
				env.getInteger("database.pool-size")
		), logger);
	}

	protected abstract List<Table> getDataSet();
	
	@Before
	@BeforeEach
	public void before() throws SQLException {
		database.migrate();
		applyDataSet();
	}
	
	@After
	@AfterEach
	public void after() throws SQLException {
		database.rollback();
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