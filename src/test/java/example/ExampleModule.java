package example;

import java.util.Arrays;
import java.util.List;

import ji.common.Logger;
import ji.database.Database;
import example.dao.ExampleDao;
import example.web.controllers.ExamplePageController;
import example.web.controllers.SignPageController;
import example.web.controllers.api.ExampleApiController;
import example.web.controllers.api.SignApiController;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.registr.Registr;
import toti.url.Link;
import ji.translator.Translator;
import ji.common.functions.Env;

public class ExampleModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
		AuditTrail auditTrail = new AuditTrail();
		ExampleDao dao = new ExampleDao(registr.getService("database", Database.class));
		
		registr.addFactory(SignPageController.class, (trans, iden, author, authen)->new SignPageController());
		registr.addFactory(SignApiController.class, (trans, iden, author, authen)->new SignApiController(iden, authen));
		
		registr.addFactory(ExamplePageController.class, (trans, iden, author, authen)->new ExamplePageController(trans));
		registr.addFactory(ExampleApiController.class, (trans, iden, author, authen)->new ExampleApiController(dao, logger, auditTrail, trans, iden));
		return Arrays.asList(
			new ExampleTask(dao, registr.getService(Translator.class))
		);
	}

	@Override
	public void addRoutes(Router router) {
		// router.addUrl("", "/example-module/example/list");
		//router.setLinkPattern("</[path]>/[controller]/[method]</[param]>");
		router.addUrl("", Link.get().create(ExamplePageController.class, c->c.grid()));
		router.setRedirectOnNotLogedUser(Link.get().create(SignPageController.class, c->c.grid()));
	}

	@Override
	public String getTemplatesPath() {
		return "jsp/example/resources";
	}

	@Override
	public String getControllersPath() {
		return "example/web/controllers";
	}

	@Override
	public String getTranslationPath() {
		return "jsp/example/resources";
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
