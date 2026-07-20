package toti.core.application;

import java.util.List;

import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.register.Register;
import toti.lib.files.env.Env;

public interface Module {
	
	String getName();

	List<Task> init(Env env, Register register, Link link, Router router);

}
