package toti.tutorial1;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.tutorial1.services.DeviceStateDao;
import toti.tutorial1.services.DevicesDao;
import toti.tutorial1.tasks.DeviceStateCheckTask;
import toti.tutorial1.web.DevicePageController;
import toti.tutorial1.web.SignPageController;
import toti.tutorial1.web.StatePageController;
import toti.tutorial1.web.WelcomePageController;
import toti.tutorial1.web.api.DeviceApiController;
import toti.tutorial1.web.api.SignApiController;
import toti.tutorial1.web.api.StateApiController;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.register.Register;
import toti.url.Link;

public class EdgeControlModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		DevicesDao deviceDao = new DevicesDao(database);
		DeviceStateDao stateDao = new DeviceStateDao(database);
		
		register.addFactory(WelcomePageController.class, ()->new WelcomePageController());
		register.addFactory(
			SignPageController.class, 
			(transl, identity, authorizator, authenticator)->new SignPageController(transl)
		);
		register.addFactory(
			SignApiController.class,
			(transl, identity, authorizator, authenticator)->new SignApiController(authenticator, transl, identity)
		);
		register.addFactory(
			DevicePageController.class,
			(transl, identity, authorizator, authenticator)->new DevicePageController(translator)
		);
		register.addFactory(
			DeviceApiController.class,
			(transl, identity, authorizator, authenticator)->new DeviceApiController(deviceDao, translator, logger)
		);

		register.addFactory(
			StatePageController.class,
			(transl, identity, authorizator, authenticator)->new StatePageController(translator)
		);
		register.addFactory(
			StateApiController.class,
			(transl, identity, authorizator, authenticator)->new StateApiController(stateDao, logger)
		);
		
		return Arrays.asList(
			new DeviceStateCheckTask(deviceDao, stateDao, logger)
		);
	}

	@Override
	public void addRoutes(Router router) {
		router.addUrl("/", Link.get().create(WelcomePageController.class, c->c.welcomePage()));
		router.setRedirectOnNotLoggedInUser(Link.get().create(SignPageController.class, c->c.loginPage(null)));
	}
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getControllersPath() {
		return "toti/tutorial1/web";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/tutorial1/templates/jsp";
	}
	
	@Override
	public String getTranslationPath() {
		return "translations";
	}
	
	@Override
	public String getMigrationsPath() {
		return "toti/tutorial1/migrations";
	}

}