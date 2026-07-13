package toti.extension.database;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.logging.log4j.Logger;

import toti.core.answers.Headers;
import toti.core.answers.request.Identity;
import toti.core.application.register.Register;
import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.database.base.Database;
import toti.lib.database.base.DatabaseConfig;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;

public class DatabaseExtension implements Extension {

	private final Logger logger;
	
	private BiFunction<List<String>, Env, Database> createDatabase = null;
	private Database database;
	private final List<String> migrationPaths = new LinkedList<>();
	
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
		Env env = appEnv.getSection("database");
		if (createDatabase != null) {
			this.database = createDatabase.apply(migrationPaths, env);
		} else if (env != null && env.getString("type") != null) {
			DatabaseConfig config = new DatabaseConfig(
				env.getString("type"),
				env.getString("url"),
				env.getString("schema-name"),
				env.getString("login"),
				env.getString("password"),
				migrationPaths,
				env.getInteger("pool-size")
			);
			env.getSection("options").iterate((name, value)->{
				config.setProperty(name, value.getValue());
			});
			this.database = new Database(config, logger);
		}
		if (this.database == null) {
			logger.info("No database specified");
		}
	}

	@Override
	public void onApplicationStart() throws Exception {
		if (database != null) {
			database.createDbIfNotExists();
			database.migrate();
		}
	}

	@Override
	public void onApplicationStop() throws Exception {}
	
	public Database getDatabase() {
		return database;
	}
	
	public void addMigrationPath(String migrationPath) {
		migrationPaths.add(migrationPath);
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}

}
