package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.socketCommunication.SslCredentials;
import toti.logging.TotiLoggerFactory;
import toti.register.Register;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;

public class Application {
	
	public static String APP_CONFIG_FILE = "conf/app.properties";
	
	private HttpServer server;
	private Database database;
	private final Logger logger;

	public <T extends Module> Application(List<T> modules) {
		this(modules, null);
	}

	public <T extends Module> Application(List<T> modules, Function<String, Logger> loggerFactory) {
		if (loggerFactory == null) {
			loggerFactory = TotiLoggerFactory.get();
		}
		this.logger = loggerFactory.apply("toti");
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
				database = new Database(databaseConfig, loggerFactory.apply("database"));
			}
			/*** init class ****/
			this.server = createServerFactory(env, loggerFactory).get(modules, env, database);
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
	
	public Register getRegister() {
		return server.getRegister();
	}
	
	public void start() {
		logger.info("Starting...");
		try {
			if (database != null) {
				database.createDbAndMigrate();
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
			server.stop();
		} catch (Exception e) {
			logger.error("Stoped with failure", e);
			System.exit(0);
		}
		logger.info("Stopped");
	}
	
	/************/
	
	public HttpServerFactory createServerFactory(Env env, Function<String, Logger> loggerFactory) throws Exception {
		HttpServerFactory factory = new HttpServerFactory();
		factory.setLoggerFactory(loggerFactory);
		if (env != null) {
			if (env.getString("http.url-pattern") != null) {
				factory.setUrlPattern(env.getString("http.url-pattern"));
			}
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
				Headers headers = new Headers();
				env.getList("http.headers", "\\|").forEach(h->{
					String[] hds = h.split(":", 2);
					if (hds.length == 1) {
						headers.addHeader(hds[0].trim(), "");
					} else {
						headers.addHeader(hds[0].trim(), hds[1].trim());
					}
				});
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
			if (env.getString("http.logs-path") != null) {
				factory.setLogsPath(env.getString("http.logs-path"));
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
			if (env.getString("http.dir-default-file") != null) {
				factory.setDirDefaultFile(env.getString("http.dir-default-file").isEmpty() ? null : env.getString("http.dir-default-file"));
			}
			if (env.getString("lang.locales") != null) {
				List<Locale> locales = new LinkedList<>();
				for (String l : env.getString("lang.locales").split(",")) {
					String locale = l.trim();
					Boolean ltr = env.getBoolean("lang.locale." + locale + ".ltr");
					String substitutions = env.getString("lang.locale." + locale + ".substitutions");
					locales.add(new Locale(
						locale,
						ltr == null ? true : ltr,
						substitutions == null ? Arrays.asList() : Arrays.asList(substitutions.split(",")) 
					));
				}
				factory.setLanguageSettings(new LanguageSettings(
					env.getString("lang.default"),
					locales
				));
			}
			if (env.getString("http.token-expired") != null) {
				factory.setTokenExpirationTime(env.getLong("http.token-expired"));
			}
			if (env.getString("http.token-salt") != null) {
				factory.setTokenCustomSalt(env.getString("http.token-salt"));
			}
			
			SslCredentials cred = new SslCredentials();
			if (env.getString("http.key-store") != null && env.getString("http.key-store-password") != null) {
				cred.setCertificateStore(
					env.getString("http.key-store"),
					env.getString("http.key-store-password")
				);
			}
			if (env.getString("http.trust-store") != null && env.getString("http.trust-store-password") != null) {
				cred.setTrustedClientsStore(
					env.getString("http.trust-store"),
					env.getString("http.trust-store-password")
				);
			} else {
				cred.setTrustAll();
			}
			if (env.getString("http.trust-store") != null || env.getString("http.key-store") != null) {
				factory.setCerts(cred);
			}
			
			if (env.getString("http.ip") != null) {
				factory.setDevelopIpAdresses(env.getList("http.ip", "\\|"));
			}
			if (env.getString("http.max-request-size") != null) {
				factory.setMaxRequestBodySize(env.getInteger("http.max-request-size"));
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
