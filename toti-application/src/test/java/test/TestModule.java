package test;

import java.util.Arrays;
import java.util.List;

import ji.common.functions.Env;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

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
