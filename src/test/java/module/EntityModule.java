package module;

import java.util.Arrays;
import java.util.List;

import common.Logger;
import database.Database;
import module.controllers.EntityPageController;
import module.controllers.api.EntityApiController;
import module.controllers.api.EntityDao;
import module.controllers.api.EntityDaoDatabase;
import module.controllers.api.SignApiController;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.registr.Registr;
import common.functions.Env;

public class EntityModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
		EntityDao dao = new EntityDaoDatabase(database);
			
		registr.addFactory(EntityPageController.class, (trans, iden, athor, authen)->{
			return new EntityPageController(trans);
		});
		registr.addFactory(EntityApiController.class, (trans, iden, athor, authen)->{
			return new EntityApiController(dao, logger, new AuditTrail(), trans);
		});
		registr.addFactory(SignApiController.class, (trans, iden, athor, authen)->{
			return new SignApiController(iden, authen);
		});
		return Arrays.asList();
	}

	@Override
	public void addRoutes(Router router) {}

	@Override
	public String getTemplatesPath() {
		return "module/resources";
	}

	@Override
	public String getControllersPath() {
		return "module/controllers";
	}

	@Override
	public String getTranslationPath() {
		return "module/resources/module";
	}

	@Override
	public String getMigrationsPath() {
		return "module/resources";
	}

	@Override
	public String getName() {
		return "entity";
	}

}
