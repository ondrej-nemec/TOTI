package toti.samples;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Console;
import ji.common.functions.Env;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.samples.developtools.DevelopToolsExample;
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
			new DevelopToolsExample(),
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

		try {
			// select one of the runs, then http://localhost:8080/toti
			runner.setProgramatically(wait);
			// runner.configFromFile(wait);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final List<Module> modules;
	private final String confFilePath;
	
	public TotiSamplesRunner(List<Module> modules, String confFilePath) {
		this.modules = modules;
		this.confFilePath = confFilePath;
	}
	
	public void configFromFile(Supplier<Void> wait) throws Exception {
		HttpServerFactory serverFactory = new HttpServerFactory(confFilePath);
		HttpServer server = serverFactory.create();
		server.addApplication("localhost", (evn, factory)->{
			return factory.create(modules);
		});
		
		// start() IS NOT BLOCKING
		server.start();

		// wait on stop signal
		wait.get();
		
		// if stop fail, System.exit is called
		server.stop();
	}
	
	public void setProgramatically(Supplier<Void> wait) throws Exception {
		// env can be null
		Env dbEnv = new Env(confFilePath); // config with db credentials
		
		HttpServerFactory serverFactory = new HttpServerFactory()
			// calling settings...
			.setPort(8080)
			.setMaxRequestBodySize(1024 * 10); // 10 kB - !! for all body;
		HttpServer server = serverFactory.create();
		server.addApplication("localhost", (evn, factory)->{
			return factory
				.setTokenExpirationTime(30 * 1000) // 30s
				.setLanguageSettings(new LanguageSettings(Arrays.asList(new Locale("en", true, Arrays.asList()))))
				// try with or without following line
				// .setDevelopIpAdresses(Arrays.asList()) // no develop ips
				// try change URL pattern
				// .setUrlPattern("/api/[controller]/[method]</[param]></[param]>")
				.setUseProfiler(true)
				
				.setDatabase((migrations, env)->{
					return new Database(
						new DatabaseConfig(
							dbEnv.getString("database.type"),
							dbEnv.getString("database.url"), 
							false, 
							dbEnv.getString("database.schema-name"), 
							dbEnv.getString("database.login"), 
							dbEnv.getString("database.password"),
							migrations,
							1 // thread pool, 1 is enought in example
						),
						mock(Logger.class) // real logger is not required
					);
				})
				
				.create(modules);
		});
				
		server.start();// start IS NOT BLOCKING

		// wait on stop signal
		wait.get();
			
		/* stop */
		server.stop();
	}
	
}
