package ji.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.database.support.ConnectionFunction;
import ji.database.support.DoubleConsumer;
import ji.database.support.QueryBuilderFunction;
import ji.database.support.SqlQueryProfiler;
import ji.database.wrappers.ConnectionWrapper;
import ji.migration.MigrationTool;
import ji.querybuilder.QueryBuilder;

public class Database {
	
	private SqlQueryProfiler profiler;
	
	private final DatabaseConfig config;
	
	private final Logger logger;
	
	protected final DatabaseInstance instance;
	
	protected final ConnectionPool pool;

	public Database(DatabaseConfig config, Logger logger) {
		this(config, false, null, logger);
	}

	public Database(DatabaseConfig config, SqlQueryProfiler profiler, Logger logger) {
		this(config, false, profiler, logger);
	}

	protected Database(DatabaseConfig config, boolean isTemp, SqlQueryProfiler profiler, Logger logger) {
		this.config = config;
		this.logger = logger;
		this.profiler = profiler == null ? createEmptyProfiler() : profiler;
		this.pool = new ConnectionPool(createSchemaConnectionString(), createProperties(), config.poolSize, logger, isTemp, this.profiler);
		this.instance = createInstance(config.schemaName, logger);
	}

	protected Database(DatabaseConfig config, DatabaseInstance instance, ConnectionPool pool, Logger logger) {
		this.config = config;
		this.logger = logger;
		this.instance = instance;
		this.pool = pool;
	}
	
	private SqlQueryProfiler createEmptyProfiler() {
		return new SqlQueryProfiler() {
			@Override public void prepare(String identifier, String sql) {}
			@Override public void executed(String identifier, Object res) {}
			@Override public void execute(String identifier) {}
			@Override public void execute(String identifier, String sql) {}
			@Override public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {}
			@Override public void addParam(String identifier, Object param) {}
		};
	}
	
	private DatabaseInstance createInstance(String name, Logger logger) {
		switch (config.type) {
		case "derby":
			return new Derby(
					config.runOnExternalServer, 
					config.pathOrUrlToLocation, 
					createSchemaConnectionString(),
					createProperties(),
					logger
			);
		case "mysql":
			return new MySql(config.runOnExternalServer, createDatabaseConnectionString(), createProperties(), name, logger);
		case "postgresql":
			return new PosgreSql(config.runOnExternalServer, createDatabaseConnectionString(), createProperties(), name, logger);
		case "sqlserver":
			return new SqlServer(config.runOnExternalServer, createDatabaseConnectionString(), createProperties(), name, logger);
		case "sqlite":
			return new SqLite(config.runOnExternalServer, createDatabaseConnectionString(), createProperties(), name, logger);
		default:
			throw new RuntimeException("Unsupported type " + config.type);
		}
	}
	
	/************ API ***********/
	
	public void startServer() {
		instance.startServer();
	}
	
	public void stopServer() {
		instance.stopServer();
	}
	
	public <T> T applyQuery(final ConnectionFunction<T> consumer) throws SQLException {
		return getDoubleFunction(consumer).get();
	}
	
	public <T> T applyBuilder(final QueryBuilderFunction<T> consumer) throws SQLException {
		return getDoubleFunction((con)->{
			return consumer.apply(getQueryBuilder(con));
		}).get();
	}
	
	public Connection getConnection() throws SQLException {
		return new ConnectionWrapper(pool.getConnection(), profiler) {
			@Override
			public void close() throws SQLException {
				// super.close();
				pool.returnConnection(getConnection());
			}
		};
	}

	/***************************/
	
	protected <T> DoubleConsumer<T> getDoubleFunction(ConnectionFunction<T> consumer) {
		return ()->{
			Connection con = pool.getConnection();
			try {
				con.setAutoCommit(false);
				T t = consumer.apply(con);
				con.commit();
				pool.returnConnection(con);
				return t;
			} catch (Exception e) {
				con.rollback();
				pool.returnConnection(con);
				throw new SQLException("Error in database consumer. Transaction rollback.", e);
			}
		};
	}
	
	/********* CONNECTION STRING **********/
	
	private String createDatabaseConnectionString() {
		return "jdbc:" + config.type + ":" + config.pathOrUrlToLocation + "/";
	}
	
	private String createSchemaConnectionString() {
		return createDatabaseConnectionString() + config.schemaName;
	}
	
	private Properties createProperties() {
		Properties props = new Properties();
		props.setProperty("user", config.login);
		props.setProperty("password", config.password);
		props.setProperty("serverTimezone", ZoneId.systemDefault().toString());
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		return props;
	}
	
	private QueryBuilder getQueryBuilder(Connection connection) {
		return new QueryBuilder(instance.getBuilderInstance(), connection);
	}
	
	/********* Migration ****************/
	
	public boolean createDbAndMigrate() {
		try {
			createDbIfNotExists();
			migrate();
			logger.info("All migrations were applied");
		} catch (SQLException e) {
			logger.fatal("Create db and migrante fail", e);
			return false;
		}
		return true;
	}
	
	public void createDbIfNotExists() throws SQLException {
		instance.createDb();
		logger.info("DB schema was created");
	}
	
	public void migrate() throws SQLException {
		applyBuilder((builder)->{
			MigrationTool tool = new MigrationTool(config.pathToMigrations, builder, logger);
			try {
				tool.migrate();
			} catch (Exception e) {
				throw new SQLException(e);
			}
			return null;
		});
	}
	
}
