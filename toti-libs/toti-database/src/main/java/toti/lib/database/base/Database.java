package toti.lib.database.base;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import toti.lib.database.base.support.ConnectionFunction;
import toti.lib.database.base.support.DoubleConsumer;
import toti.lib.database.base.support.QueryBuilderConsumer;
import toti.lib.database.base.support.QueryBuilderFunction;
import toti.lib.database.base.support.SqlQueryProfiler;
import toti.lib.database.base.wrappers.ConnectionWrapper;
import toti.lib.database.migration.MigrationTool;
import toti.lib.database.querybuilder.QueryBuilder;

public class Database implements AutoCloseable {
	
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
		this.instance = createInstance(config.schemaName, logger);
		this.pool = new ConnectionPool(instance.getConnectionString(), config.getProperties(), config.poolSize, logger, isTemp, this.profiler);
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
		return switch (config.type) {
		/*case "derby":
			return new Derby( 
					config.pathOrUrlToLocation, 
					createSchemaConnectionString(),
					createProperties(),
					logger
			);*/
			case "mysql"->new MySql(createDatabaseConnectionString(), config.getProperties(), name, logger);
			case "postgresql"->new PosgreSql(createDatabaseConnectionString(), config.getProperties(), name, logger);
			case "sqlserver"->new SqlServer(createDatabaseConnectionString(), config.getProperties(), name, logger);
			case "sqlite"->new SqLite(createDatabaseConnectionString(), config.getProperties(), name, logger);
			default->throw new RuntimeException("Unsupported type " + config.type);
		};
	}
	
	/************ API ***********/
/*
	public void startServer() {
		instance.startServer();
	}
	
	public void stopServer() {
		instance.stopServer();
	}
*/
	public <T> T applyQuery(final ConnectionFunction<T> consumer) throws SQLException {
		return getDoubleFunction(consumer).get();
	}
	
	public <T> T applyBuilder(final QueryBuilderFunction<T> consumer) throws SQLException {
		return getDoubleFunction((con)->{
			return consumer.apply(getQueryBuilder(con));
		}).get();
	}
	
	public void applyBuilder(final QueryBuilderConsumer consumer) throws SQLException {
		getDoubleFunction((con)->{
			consumer.accept(getQueryBuilder(con));
			return null;
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

	@Override
	public void close() throws SQLException {
		pool.close();
	}

	/***************************/
	
	@SuppressWarnings("UseSpecificCatch")
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
		return "jdbc:" + config.type + ":" + config.pathOrUrlToLocation;
	}
/*
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
*/
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
