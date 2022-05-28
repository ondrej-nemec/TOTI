package toti.templating.parameters;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.Module;
import toti.annotations.Action;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;

@toti.annotations.Controller("contr")
public class Controller implements Module {

	@Action("func")
	public Response index() {
		return null;
	}
	
	@Override
	public String getName() {
		return "params";
	}

	@Override
	public String getControllersPath() {
		return "toti/templating/parameters";
	}
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(Controller.class, ()->new Controller());
		return Arrays.asList();
	}

}
