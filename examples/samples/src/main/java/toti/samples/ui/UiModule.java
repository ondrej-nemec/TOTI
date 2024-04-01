package toti.samples.ui;

import java.util.Arrays;
import java.util.List;

import ji.common.functions.Env;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

public class UiModule implements Module {

	@Override
	public String getName() {
		return "ui";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		return Arrays.asList();
	}

}
