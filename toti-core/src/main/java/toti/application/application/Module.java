package toti.application.application;

import java.util.List;

import toti.application.answers.router.Link;
import toti.application.answers.router.Router;
import toti.application.application.register.Register;
import toti.lib.files.env.Env;

public interface Module {
	
	String getName();

	List<Task> initInstances(Env env, Register register, Link link) throws Exception;
	
	default void addRoutes(Router router, Link link) {}
/*
	default String getTranslationPath() {
		return null;
	}
	
	default String getMigrationsPath() {
		return null;
	}
	
	default String getTemplatesPath() {
		return null;
	}
*/
}
