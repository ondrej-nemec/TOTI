package module;

import java.util.ArrayList;
import java.util.List;

import common.Logger;
import database.Database;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.registr.Registr;
import utils.Env;

public class ModuleConfig implements Module {

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
		registr.addFactory(ModuleController.class, ()->{
			return new ModuleController();
		});
		return new ArrayList<>();
	}

	@Override
	public void addRoutes(Router router) {}

	@Override
	public String getTemplatesPath() {
		return "jsp";
	}

	@Override
	public String getControllersPath() {
		return "module";
	}

	@Override
	public String getName() {
		return "module";
	}

	@Override
	public String getTranslationPath() {
		return "translations/module";
	}

	@Override
	public String getMigrationsPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
