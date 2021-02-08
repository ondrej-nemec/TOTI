package toti.application;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import database.Database;
import database.DatabaseConfig;
import logging.LoggerFactory;
import socketCommunication.ServerSecuredCredentials;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.ResponseHeaders;
import toti.registr.Registr;
import utils.Env;

public class Application {

	public static void main(String[] args) {
		// or api for service,...
		Application a = new Application(Arrays.asList());
		a.start();
	}
	
	/****************/
	
	public static String APP_CONFIG_FILE = "conf/app.properties";
	public static String LOG_CONFIG_FILE = "conf/ji-logging.properties";
	
	public static final String USER_SECURITY_SERVICE = "user-security-service";
	
	private HttpServer server;
	private Database database;
	private final List<Task> tasks = new LinkedList<>();
	
	public Application(List<Module> modules) {
		LoggerFactory.setConfigFile(LOG_CONFIG_FILE);
		LoggerFactory.getLogger("main").info("Initialization...");
		try {
			Env env = null;
			if (APP_CONFIG_FILE == null) {
				LoggerFactory.getLogger("main").warn("No config file specified. Env is null");
			} else {
				env = new Env(APP_CONFIG_FILE);
			}
			/*** creating db ****/
			List<String> migrations = new LinkedList<>();
			modules.forEach((config)->{
				migrations.add(config.getMigrationsPath());
			});
			DatabaseConfig databaseConfig = createDatabaseConfig(env, migrations);
			if (databaseConfig == null) {
				database = null;
				LoggerFactory.getLogger("main").warn("Database config is null, so database is null");
			} else {
				database = new Database(databaseConfig, LoggerFactory.getLogger("database"));
			}
			/*** init classes ****/
			Registr registr = Registr.get();
			registr.addService("database", database);
			for (Module module : modules) {
				tasks.addAll(module.initInstances(
					env,
					registr,
					database,
					LoggerFactory.getLogger(module.getName())
				));
			}
			this.server = createServerFactory(env, registr).get(modules);
		} catch (Exception e) {
			LoggerFactory.getLogger("main").error("Start failed", e);
			System.exit(1);
		}
		LoggerFactory.getLogger("main").info("Initialized");
	}
	
	/************/
	
	public void start() {
		LoggerFactory.getLogger("main").info("Starting...");
		try {
			if (database != null) {
				database.createDbAndMigrate();
			}
			for (Task task : tasks) {
				task.start();
			}
			server.start();
		} catch (Exception e) {
			LoggerFactory.getLogger("main").error("Start failed", e);
			System.exit(1);
		}
		LoggerFactory.getLogger("main").info("Started");
	}
	
	public void stop() {
		LoggerFactory.getLogger("main").info("Stopping...");
		try {
			for (Task task : tasks) {
				task.stop();
			}
			server.stop();
		} catch (Exception e) {
			LoggerFactory.getLogger("main").error("Stoped with failure", e);
			System.exit(0);
		}
		LoggerFactory.getLogger("main").info("Stopped");
	}
	
	/************/
	
	public HttpServerFactory createServerFactory(Env env, Registr registr) throws Exception {
		HttpServerFactory factory = new HttpServerFactory();
		if (env != null) {
			if (env.getString("http.port") != null) {
				factory.setPort(env.getInt("http.port"));
			}
			if (env.getString("http.thread-pool") != null) {
				factory.setThreadPool(env.getInt("http.thread-pool"));
			}
			if (env.getString("http.read-timeout") != null) {
				factory.setReadTimeout(env.getInt("http.read-timeout"));
			}
			if (env.getString("http.headers") != null) {
				factory.setHeaders(new ResponseHeaders(env.getList("http.headers", "\\|")));
			}
		//	if (env.getString("") != null) {
				factory.setLogger(LoggerFactory.getLogger("server"));
		//	}
			if (env.getString("http.charset") != null) {
				factory.setCharset(env.getString("http.charset"));
			}
			if (env.getString("http.temp") != null) {
				factory.setTempPath(env.getString("http.temp"));
			}
			if (env.getString("http.resource-path") != null) {
				factory.setResourcesPath(env.getString("http.resource-path"));
			}
			if (env.getString("http.minimalize-templates") != null) {
				factory.setMinimalize(env.getBoolean("http.minimalize-templates"));
			}
			if (env.getString("http.minimalize-templates") != null) {
				factory.setDeleteTempJavaFiles(env.getBoolean("http.delete-temp-java"));
			}
			if (env.getString("http.dir-allowed") != null) {
				factory.setDirResponseAllowed(env.getBoolean("http.dir-allowed"));
			}
			if (env.getString("http.locale") != null) {
				factory.setDefLang(env.getString("http.locale"));
			}
			if (env.getString("http.token-expired") != null && env.getString("http.token-salt") != null) {
				try {
					factory.setUserSecurity(registr.getService(USER_SECURITY_SERVICE, UserSecurityFactory.class).get(
						env.getInt("http.token-expired"),
						env.getString("http.token-salt"),
						LoggerFactory.getLogger("auth")
					));
				} catch (RuntimeException ignored) {}
			}
			if (env.getString("http.key-store") != null && env.getString("http.key-store-password") != null) {
				factory.setCerts(new ServerSecuredCredentials(
					env.getString("http.key-store"),
					env.getString("http.key-store-password"),
					env.getString("http.trust-store") != null ? Optional.of(env.getString("http.trust-store")) : Optional.empty(),
					env.getString("http.trust-store-password") != null ? Optional.of(env.getString("http.trust-store-password")) : Optional.empty()
				));
			}
			if (env.getString("http.ip") != null) {
				factory.setDevelopIpAdresses(env.getList("http.ip", "\\|"));
			}
			/*
			private Translator translator;
			private int maxUploadFileSize = 0;
			private Optional<List<String>> allowedUploadFileTypes = Optional.of(new LinkedList<>());
			*/
		}
		return factory;
	}
	
	public DatabaseConfig createDatabaseConfig(Env env, List<String> migrations) {
		return new DatabaseConfig(
				env.getString("database.type"),
				env.getString("database.url"),
				env.getBoolean("database.external"),
				env.getString("database.schema-name"),
				env.getString("database.login"),
				env.getString("database.password"),
				migrations,
				env.getString("database.timezone"),
				env.getInt("database.pool-size")
		);
	}
	
}
