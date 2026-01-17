package test;

import java.util.Arrays;
import java.util.List;

import toti.application.answers.router.Link;
import toti.application.application.Module;
import toti.application.application.Task;
import toti.application.application.register.Register;
import toti.lib.files.env.Env;

public class TestModule implements Module {

	@Override
	public String getName() {
		return "testingModule";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		register.addController(ControllerA.class, ()->new ControllerA());
		register.addController(ControllerC.class, ()->new ControllerC());
		return Arrays.asList();
	}

}
