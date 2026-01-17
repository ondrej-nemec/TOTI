package toti.samples.security;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import toti.env.Env;
import toti.lib.database.base.Database;
import ji.translator.Translator;
import toti.application.answers.router.Link;
import toti.application.answers.router.Router;
import toti.application.application.Module;
import toti.application.application.Task;
import toti.application.application.register.Register;

/**
 * Example demonstrate security in TOTI
 * @author Ondřej Němec
 *
 */
public class SecurityExample implements Module {
	
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		SessionManager session = new SessionManager();
		register.setSessionUserProvider(session); // VERY IMPORTANT
		register.addController(SignController.class, ()->new SignController(link, session));
		register.addController(PermissionsController.class, ()->new PermissionsController(link, session));
		return Arrays.asList();
	}
	
	@Override
	public void addRoutes(Router router, Link link) {
		//*
		// redirect to async login
		// TODO router.setRedirectOnNotLoggedInUser(router.getLink().create(SignExample.class, c->c.asyncLoginPage(null)));
		/*/
		// redirect to sync login
		router.setRedirectOnNotLoggedInUser(router.getLink().create(SignExample.class, c->c.syncLoginPage(null)));
		//*/
	}

	@Override
	public String getName() {
		return "examples-security";
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/security";
	}

}
