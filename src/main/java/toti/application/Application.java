package toti.application;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import common.Logger;
import common.functions.Env;
import common.functions.InputStreamLoader;
import common.structures.DictionaryValue;
import common.structures.MapDictionary;
import common.structures.ThrowingBiFunction;
import core.text.Text;
import core.text.basic.ReadText;
import database.Database;
import database.DatabaseConfig;
import socketCommunication.ServerSecuredCredentials;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.ResponseHeaders;
import toti.logging.TotiLogger;
import toti.registr.Registr;
import toti.security.User;
import translator.LanguageSettings;
import translator.Locale;
import translator.Translator;

public class Application {
	
	public static String APP_CONFIG_FILE = "conf/app.properties";
	
	public static final String USER_SECURITY_SERVICE = "user-security-service";
	
	private HttpServer server;
	private Database database;
	private final List<Task> tasks = new LinkedList<>();
	private final Logger logger;
	
	public <T extends Module> Application(
			List<T> modules, 
			ThrowingBiFunction<String, Registr, User, Exception> userFactory) {
		this(modules, userFactory, null);
	}
	
	public <T extends Module> Application(
			List<T> modules, 
			ThrowingBiFunction<String, Registr, User, Exception> userFactory,
			Logger logger) {
		if (logger == null) {
			logger = TotiLogger.getLogger("toti");
		}
		this.logger = logger;
		logger.info("Initialization...");
		try {
			Env env = null;
			if (APP_CONFIG_FILE == null) {
				logger.warn("No config file specified. Env is null");
			} else {
				env = new Env(APP_CONFIG_FILE);
			}
			/*** creating db ****/
			List<String> migrations = new LinkedList<>();
			modules.forEach((config)->{
				if (config.getMigrationsPath() != null) {
					migrations.add(config.getMigrationsPath());
				}
			});
			DatabaseConfig databaseConfig = createDatabaseConfig(env, migrations);
			if (databaseConfig == null) {
				database = null;
				logger.warn("Database config is null, so database is null");
			} else {
				database = new Database(databaseConfig, TotiLogger.getLogger("database"));
			}
			/*** init classes ****/
			Registr registr = Registr.get();
			registr.addService("database", database);
			this.server = createServerFactory(env, registr).get(modules, (content)->{
				return userFactory.apply(content, registr);
			});
			// TODO fix - move to module
			registr.addService(Translator.class.getName(), server.getTranslator());
			for (Module module : modules) {
				tasks.addAll(module.initInstances(
					env,
					server.getTranslator(),
					registr,
					database,
					TotiLogger.getLogger(module.getName())
				));
			}
		} catch (Exception e) {
			logger.error("Start failed", e);
			System.exit(1);
		}
		logger.info("Initialized");
	}
	
	/************/
	
	public Translator getTranslator() {
		return server.getTranslator();
	}
	
	public void start() {
		logger.info("Starting...");
		try {
			if (database != null) {
				database.createDbAndMigrate();
			}
			for (Task task : tasks) {
				task.start();
			}
			server.start();
		} catch (Exception e) {
			logger.error("Start failed", e);
			System.exit(1);
		}
		logger.info("Started");
	}
	
	public void stop() {
		logger.info("Stopping...");
		try {
			for (Task task : tasks) {
				task.stop();
			}
			server.stop();
		} catch (Exception e) {
			logger.error("Stoped with failure", e);
			System.exit(0);
		}
		logger.info("Stopped");
	}
	
	/************/
	
	public HttpServerFactory createServerFactory(Env env, Registr registr) throws Exception {
		HttpServerFactory factory = new HttpServerFactory(logger);
		// factory.setLogger(logger);
		if (env != null) {
			if (env.getString("http.port") != null) {
				factory.setPort(env.getInteger("http.port"));
			}
			if (env.getString("http.thread-pool") != null) {
				factory.setThreadPool(env.getInteger("http.thread-pool"));
			}
			if (env.getString("http.read-timeout") != null) {
				factory.setReadTimeout(env.getInteger("http.read-timeout"));
			}
			if (env.getString("http.headers") != null) {
				factory.setHeaders(new ResponseHeaders(env.getList("http.headers", "\\|")));
			}
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
			if (env.getString("http.delete-temp-java") != null) {
				factory.setDeleteTempJavaFiles(env.getBoolean("http.delete-temp-java"));
			}
			if (env.getString("http.dir-allowed") != null) {
				factory.setDirResponseAllowed(env.getBoolean("http.dir-allowed"));
			}
			if (env.getString("http.locale-settings") != null) {
				MapDictionary<String, Object> config = new DictionaryValue(
					Text.get().read((br)->{
						return ReadText.get().asString(br);
					}, 
					InputStreamLoader.createInputStream(getClass(), env.getString("http.locale-settings"))
				)).getDictionaryMap();
				List<Locale> locales = new LinkedList<>();
				config.getDictionaryMap("locales").forEach((locale, setting)->{
					locales.add(new Locale(
						locale.toString(), 
						setting.getDictionaryMap().getBoolean("isLeftToRight"),
						setting.getDictionaryMap().getList("substitutions")
					));
				});
				factory.setLanguageSettings(new LanguageSettings(
					config.getString("default"),
					locales
				));
				//factory.setDefLang(env.getString("http.locale"));
			}
			if (env.getString("http.token-expired") != null) {
				factory.setTokenExpirationTime(env.getLong("http.token-expired"));
			}
			if (env.getString("http.token-salt") != null) {
				factory.setTokenCustomSalt(env.getString("http.token-salt"));
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
			if (env.getString("http.max-upload-size") != null) {
				factory.setMaxUploadFileSize(env.getInteger("http.max-upload-size"));
			}
			if (env.getString("http.allowed-file-types") != null) {
				factory.setAllowedUploadFileTypes(Optional.of(env.getList("http.allowed-file-types", "|")));
			}
			if (env.getString("http.use-profiler") != null) {
				factory.setUseProfiler(env.getBoolean("http.use-profiler"));
			}
			/*
			private Translator translator;
			*/
		}
		return factory;
	}
	
	public DatabaseConfig createDatabaseConfig(Env env, List<String> migrations) {
		if (env.getString("database.type") == null) {
			return null;
		}
		return new DatabaseConfig(
				env.getString("database.type"),
				env.getString("database.url"),
				env.getBoolean("database.external") == null ? true : env.getBoolean("database.external"),
				env.getString("database.schema-name"),
				env.getString("database.login"),
				env.getString("database.password"),
				migrations,
				env.getInteger("database.pool-size")
		);
	}
	
}
