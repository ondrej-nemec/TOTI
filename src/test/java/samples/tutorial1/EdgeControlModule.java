package samples.tutorial1;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import samples.tutorial1.web.WelcomePageController;
import toti.Module;
import toti.Router;
import toti.application.Task;
import toti.register.Register;
import toti.url.Link;

public class EdgeControlModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(WelcomePageController.class, ()->new WelcomePageController());
		return Arrays.asList();
	}

	@Override
	public void addRoutes(Router router) {
		router.addUrl("", Link.get().create(WelcomePageController.class, c->c.welcomePage()));
	}
	
	@Override
	public String getName() {
		return "main";
	}

	@Override
	public String getControllersPath() {
		return "samples/tutorial1/web";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/tutorial1/resources/jsp";
	}
	
	@Override
	public String getTranslationPath() {
		return "samples/tutorial1/resources";
	}

}
