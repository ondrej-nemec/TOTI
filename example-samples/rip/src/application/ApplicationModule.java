package toti.samples.application;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

public class ApplicationModule implements Module {
	
	// TODO
	/*
	- custom route
	- custom exception handler
	- session user provider - security vc. setRedirectOnNotLoggedInUser
	- ruzne exceptions
	- ruzne requesty
	- ruzne response
	*/
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database,
			Logger logger) throws Exception {
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public String getTemplatesPath() {
		return "templates/application";
	}
	
}
