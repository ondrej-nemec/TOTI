package toti.application;

import java.util.List;

import ji.common.functions.Env;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.register.Register;

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
