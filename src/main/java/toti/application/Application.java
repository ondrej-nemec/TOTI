package toti.application;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import database.DatabaseConfig;
import logging.LoggerFactory;
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
			Env env = new Env(APP_CONFIG_FILE);
			/*** creating db ****/
			List<String> migrations = new LinkedList<>();
			modules.forEach((config)->{
				migrations.add(config.getMigrationsPath());
			});
			database = createDatabase(env, migrations);
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
			this.server = createServer(env, modules, registr);
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
			database.createDbAndMigrate();
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
	
	private HttpServer createServer(Env env, List<Module> modules, Registr registr) throws Exception {
		// TODO create factory constructor with env
		HttpServerFactory factory = new HttpServerFactory();
		factory.setPort(env.getInt("http.port"));
		factory.setThreadPool(env.getInt("http.thread-pool"));
		factory.setReadTimeout(env.getInt("http.read-timeout"));
		factory.setHeaders(new ResponseHeaders(env.getList("http.headers", "\\|")));
		factory.setLogger(LoggerFactory.getLogger("server"));
		
		// TODO little bit another way
		try {
			factory.setUserSecurity(registr.getService(USER_SECURITY_SERVICE, UserSecurityFactory.class).get(
				env.getInt("http.token-expired"),
				env.getString("http.token-salt"),
				LoggerFactory.getLogger("auth")
			));
		} catch (RuntimeException ignored) {}
		return factory.get(modules);
	}
	
	private Database createDatabase(Env env, List<String> migrations) {
		return new Database(new DatabaseConfig(
				env.getString("database.type"),
				env.getString("database.url"),
				env.getBoolean("database.external"),
				env.getString("database.schema-name"),
				env.getString("database.login"),
				env.getString("database.password"),
				migrations,
				env.getString("database.timezone"),
				env.getInt("database.pool-size")
		), LoggerFactory.getLogger("database"));
	}
	
}
