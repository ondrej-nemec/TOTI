package toti.samples.templating;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

public class TemplateModule implements Module {

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database,
			Logger logger) throws Exception {
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "templating";
	}

}
