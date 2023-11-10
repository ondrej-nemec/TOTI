package toti.samples.security;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

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
	public void addRoutes(Router router) {
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
