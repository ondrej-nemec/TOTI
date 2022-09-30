package toti.samples;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Console;
import ji.common.functions.Env;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import toti.Application;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.samples.exceptions.ExceptionsExample;
import toti.samples.form.FormExample;
import toti.samples.grid.GridExample;
import toti.samples.requests.RequestsExample;
import toti.samples.responses.ResponsesExample;
import toti.samples.routing.RoutingExample;
import toti.samples.security.SecurityExample;
import toti.samples.sign.SignExample;
import toti.samples.template.TemplateExample;
import toti.samples.validation.ValidationExample;

public class TotiSamplesRunner {
	
	public static void main(String[] args) {
		Supplier<Void> wait = ()->{
			Console console = new Console();
			String line = console.in();
			while(!line.equals("stop")) {
				line = console.in();
			}
			return null;
		};
		TotiSamplesRunner runner = new TotiSamplesRunner(Arrays.asList(
			new ExceptionsExample(),
			new FormExample(),
			new GridExample(),
			new RequestsExample(),
			new ResponsesExample(),
			new RoutingExample(),
			new SecurityExample(),
			new SignExample(),
			new TemplateExample(),
			new ValidationExample()
		), "toti/samples/app.properties");
		// select one of the runs, then http://localhost:8080/toti
		runner.customRun(wait);
		// runner.quickRun(wait);
	}
	
	private final List<Module> modules;
	private final String confFilePath;
	
	public TotiSamplesRunner(List<Module> modules, String confFilePath) {
		this.modules = modules;
		this.confFilePath = confFilePath;
	}
	
	public void quickRun(Supplier<Void> wait) {
		Application.APP_CONFIG_FILE = confFilePath;
		Application application = new Application(modules);
		
		// if start fail, System.exit is called
		// start() IS NOT BLOCKING
		application.start();

		// wait on stop signal
		wait.get();
		
		// if stop fail, System.exit is called
		application.stop();
	}
	
	public void customRun(Supplier<Void> wait) {
		// if you wish use migrations
		List<String> migrations = new LinkedList<>();
		modules.forEach((config)->{
			if (config.getMigrationsPath() != null) {
				migrations.add(config.getMigrationsPath());
			}
		});
		
		try {
			// env can be null
			Env env = new Env("toti/samples/app.properties");
			// database can be null
			Database database = new Database(
				new DatabaseConfig(
					env.getString("db.type"),
					env.getString("db.url"), 
					false, 
					env.getString("db.schema"), 
					env.getString("db.username"), 
					env.getString("db.password"),
					migrations,
					1 // thread pool, 1 is enought in example
				),
				mock(Logger.class) // real logger is not required
			);
			HttpServer server = new HttpServerFactory()
					// calling settings...
					.setPort(8080)
					
					.setTokenExpirationTime(30 * 1000) // 30s
					.setMaxRequestBodySize(1024 * 10) // 10 kB - !! for all body
					.setLanguageSettings(new LanguageSettings(Arrays.asList(new Locale("en", true, Arrays.asList()))))
					// try with or without following line
					// .setDevelopIpAdresses(Arrays.asList()) // no develop ips
					// try change URL pattern
					// .setUrlPattern("/api/[controller]/[method]</[param]></[param]>")
					.setUseProfiler(true)
					.get(modules, env, database);
			
			/* start */
			// if you wish use migrations
			if (database != null) {
				database.createDbAndMigrate();
			}
			
			server.start();// start IS NOT BLOCKING

			// wait on stop signal
			wait.get();
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
