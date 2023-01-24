package toti.url;

import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.Module;
import toti.application.Task;
import toti.register.Register;

public class TestingModule implements Module {
	
	@Override
	public String getName() {
		return "toti";
	}

	@Override
	public String getControllersPath() {
		return "";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Link link, Database database, Logger logger) throws Exception {
		registr.addFactory(TestingController.class, (a, b, c, d)->new TestingController());
		return null;
	}
}
