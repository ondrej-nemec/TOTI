package adminer;

import java.util.ArrayList;
import java.util.List;

import common.Logger;
import database.Database;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.registr.Registr;
import utils.Env;

public class AdminerModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) {
		registr.addFactory(Adminer.class, ()->{
			return new Adminer();
		});
		return new ArrayList<>();
	}

	@Override
	public void addRoutes(Router router) {
		
	}

	@Override
	public String getTemplatesPath() {
		return "templates";
	}

	@Override
	public String getControllersPath() {
		return "adminer";
	}

	@Override
	public String getName() {
		return "adminer";
	}

	@Override
	public String getTranslationPath() {
		return "translations/adminer";
	}

	@Override
	public String getMigrationsPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
