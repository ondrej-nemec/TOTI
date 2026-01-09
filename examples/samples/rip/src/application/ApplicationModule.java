package toti.samples.application;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import toti.env.Env;
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
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "application";
	}

	/*@Override
	public String getTemplatesPath() {
		return "templates/application";
	}*/
	
}
