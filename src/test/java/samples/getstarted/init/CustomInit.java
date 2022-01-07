package samples.getstarted.init;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.application.Task;
import toti.registr.Register;

public class CustomInit {

	public static void main(String[] args) {
		Logger logger = null; // cannot be null
		Database database = null; // can be null
		Env env = null; // can be null
		List<Module> modules = Arrays.asList(
			// modules
		);
		List<Task> tasks = new LinkedList<>();
		try {
			HttpServer server = new HttpServerFactory(logger).get(modules);
			for (Module module : modules) {
				tasks.addAll(module.initInstances(
					env,
					server.getTranslator(),
					server.getRegister(),
					database,
					logger
				));
			}
			
			/* start */
			if (database != null) {
				database.createDbAndMigrate();
			}
			for (Task task : tasks) {
				task.start();
			}
			server.start();
			
			/* stop */
			for (Task task : tasks) {
				task.stop();
			}
			server.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
