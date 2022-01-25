package samples.examples.security;

import java.util.Arrays;
import java.util.List;

import example.web.controllers.api.SignApiController;
import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.application.Task;
import toti.register.Register;

/**
 * Example demonstrate authentization and authorization in TOTI
 * @author Ondřej Němec
 *
 */
public class SecurityExample implements Module {
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(
			SignApiController.class, 
			(trans, identity, authorizator, authenticator)->new SignController(identity, authenticator, logger)
		);
		register.addFactory(
			PagesController.class, 
			(trans, identity, authorizator, authenticator)->new PagesController(identity)
		);
		return null;
	}

	@Override
	public String getName() {
		return "example";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/security";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/security";
	}
	
	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new SecurityExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				.setTokenExpirationTime(30 * 1000) // 30s
				.get(modules, null, null);
			
			/* start */
			server.start();
	
			// sleep for 2min before automatic close
			try { Thread.sleep(2 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
