package example;

import java.util.Arrays;
import java.util.List;

import common.Logger;
import database.Database;
import example.dao.ExampleDao;
import example.web.controllers.ExamplePageController;
import example.web.controllers.SignPageController;
import example.web.controllers.api.ExampleApiController;
import example.web.controllers.api.SignApiController;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.registr.Registr;
import common.functions.Env;

public class ExampleModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
		AuditTrail auditTrail = new AuditTrail();
		ExampleDao dao = new ExampleDao(registr.getService("database", Database.class));
		
		registr.addFactory(SignPageController.class, (trans, iden, author, authen)->new SignPageController());
		registr.addFactory(SignApiController.class, (trans, iden, author, authen)->new SignApiController(iden, authen));
		
		registr.addFactory(ExamplePageController.class, (trans, iden, author, authen)->new ExamplePageController());
		registr.addFactory(ExampleApiController.class, (trans, iden, author, authen)->new ExampleApiController(dao, logger, auditTrail));
		return Arrays.asList();
	}

	@Override
	public void addRoutes(Router router) {
		router.addUrl("", "/example-module/example/list");
	}

	@Override
	public String getTemplatesPath() {
		return "example/resources";
	}

	@Override
	public String getControllersPath() {
		return "example/web/controllers";
	}

	@Override
	public String getTranslationPath() {
		return "example/resources";
	}

	@Override
	public String getMigrationsPath() {
		return "example/dao/migrations";
	}

	@Override
	public String getName() {
		return "example-module";
	}

}
