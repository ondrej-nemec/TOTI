package toti.database;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.database.DatabaseConfig;
import toti.application.register.Register;
import toti.extensions.ApplicationExtension;

public class DatabaseExtension implements ApplicationExtension {

	private final Logger logger;
	
	private BiFunction<List<String>, Env, Database> createDatabase = null;
	private Database database;
	private final List<String> migrations = new LinkedList<>();
	
	public DatabaseExtension(Logger logger) {
		this(null, logger);
	}
	
	public DatabaseExtension(BiFunction<List<String>, Env, Database> createDatabase, Logger logger) {
		this.createDatabase = createDatabase;
		this.logger = logger;
	}

	@Override
	public String getIdentifier() {
		return "toti-database";
	}

	@Override
	public void init(Env appEnv, Register register) {
		Env env = appEnv.getModule("database");
		if (createDatabase != null) {
			this.database = createDatabase.apply(migrations, env);
		}
		if (env != null && env.getString("type") != null) {
			this.database = new Database(new DatabaseConfig(
				env.getString("type"),
				env.getString("url"),
				env.getBoolean("external") == null ? true : env.getBoolean("external"),
				env.getString("schema-name"),
				env.getString("login"),
				env.getString("password"),
				migrations,
				env.getInteger("pool-size")
			), profiler, logger);
		}
		if (this.database == null) {
			logger.info("No database specified");
		}
	}

	@Override
	public void start() throws Exception {
		if (database != null) {
			// database.startServer();
			database.createDbIfNotExists();
			database.migrate();
		}
	}

	@Override
	public void stop() throws Exception {
		/*if (database != null) {
			database.stopServer();
		}*/
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public void addMigration(String migration) {
		migrations.add(migration);
	}

}
