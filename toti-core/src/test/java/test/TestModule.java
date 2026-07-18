package test;

import java.util.Arrays;
import java.util.List;

import toti.core.answers.router.Link;
import toti.core.application.Module;
import toti.core.application.Task;
import toti.core.application.register.Register;
import toti.lib.files.env.Env;

public class TestModule implements Module {

	@Override
	public String getName() {
		return "testingModule";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) {
		register.addController(ControllerA.class, ()->new ControllerA());
		register.addController(ControllerC.class, ()->new ControllerC());
		return Arrays.asList();
	}

}
