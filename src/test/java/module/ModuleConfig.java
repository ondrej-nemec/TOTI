package module;

import java.util.List;

import common.Logger;
import toti.Module;
import toti.Router;
import toti.Task;
import toti.registr.Registr;
import utils.Env;

public class ModuleConfig implements Module {

	@Override
	public Module initInstances(Registr registr) throws Exception {
		registr.addFactory(ModuleController.class, ()->{
			return new ModuleController();
		});
		return this;
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
	public List<Task> getTasks(Env env, Logger logger) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMigrationsPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
