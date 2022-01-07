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
		Database database = null; // can be null
		Env env = null; // can be null
		List<Module> modules = Arrays.asList(
			// modules
		);
		List<Task> tasks = new LinkedList<>();
		try {
			HttpServer server = new HttpServerFactory().get(modules, env, database);
			
			/* start */
			if (database != null) {
				database.createDbAndMigrate();
			}
			server.start();
			
			/* stop */
			server.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
