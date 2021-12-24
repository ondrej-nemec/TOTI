package toti.url;

import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import toti.Module;
import toti.application.Task;
import toti.registr.Registr;

public class TestingModule implements Module {
	
	@Override
	public String getName() {
		return "core";
	}

	@Override
	public String getControllersPath() {
		return "";
	}

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
		registr.addFactory(TestingController.class, (a, b, c, d)->new TestingController());
		return null;
	}
}
