package toti.database;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.logging.log4j.Logger;

import ji.database.Database;
import ji.database.DatabaseConfig;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.application.register.Register;
import toti.common.structures.MapDictionary;
import toti.extensions.Extension;
import toti.files.env.Env;
import toti.http.structures.RequestParameters;

public class DatabaseExtension implements Extension {

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
		Env env = appEnv.getSection("database");
		if (createDatabase != null) {
			this.database = createDatabase.apply(migrations, env);
		} else if (env != null && env.getString("type") != null) {
			DatabaseConfig config = new DatabaseConfig(
				env.getString("type"),
				env.getString("url"),
				env.getString("schema-name"),
				env.getString("login"),
				env.getString("password"),
				migrations,
				env.getInteger("pool-size")
			);
			if (env.hasSection("options")) {
				env.getSection("options").iterate((name, value)->{
					config.setProperty(name, value.getValue());
				});
			}
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
	
	public void addMigration(String migration) {
		migrations.add(migration);
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}

}
