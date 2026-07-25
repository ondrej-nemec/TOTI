package toti.examples.getStarted.core;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.Module;
import toti.core.application.Task;
import toti.core.application.register.Register;
import toti.examples.getStarted.core.controllers.CounterController;
import toti.examples.getStarted.core.controllers.WelcomeController;
import toti.examples.getStarted.core.services.Counter;
import toti.examples.getStarted.core.tasks.CounterTask;
import toti.lib.files.env.Env;

public class CoreModule implements Module {

	private final Logger logger;

	public CoreModule(Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getName() {
		return "core";
	}

	@Override
	public List<Task> init(Env env, Register register, Link link, Router router) {
		register.addController(WelcomeController.class, ()->new WelcomeController());

		Counter counter = new Counter();
		register.addController(CounterController.class, ()->new CounterController(counter));
		return Arrays.asList(new CounterTask(counter, logger));
	}

}
