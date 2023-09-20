package test;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.url.Link;

public class TestModule implements Module {

	@Override
	public String getName() {
		return "testingModule";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database,
			Logger logger) throws Exception {
		register.addController(ControllerA.class, ()->new ControllerA());
		return Arrays.asList();
	}

}
