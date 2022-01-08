package samples.getstarted.init;

import java.util.Arrays;
import java.util.List;

import ji.common.functions.Env;
import ji.database.Database;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;

public class CustomInit {

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(
			// modules
		);
		// if you wish use migrations:
		/*
		List<String> migrations = new LinkedList<>();
		modules.forEach((config)->{
			if (config.getMigrationsPath() != null) {
				migrations.add(config.getMigrationsPath());
			}
		});
		*/
		Database database = null; // can be null
		Env env = null; // can be null
		try {
			HttpServer server = new HttpServerFactory()
					// calling settings...
					.setPort(8080)
					.get(modules, env, database);
			
			/* start */
			// if you wish use migrations
			/*
			if (database != null) {
				database.createDbAndMigrate();
			}
			*/
			server.start();// start IS NOT BLOCKING

			// sleep for 2min before automatic close
			try { Thread.sleep(2 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
