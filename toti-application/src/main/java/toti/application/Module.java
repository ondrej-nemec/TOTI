package toti.application;

import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.register.Register;
import ji.translator.Translator;

public interface Module {
	
	String getName();
	
	default String getPath() {
		return getName();
	}
	
	// String getControllersPath();

	List<Task> initInstances(
		Env env, Translator translator, Register register, Link link
	) throws Exception;
	
	default void addRoutes(Router router, Link link) {}
	
	default String getTranslationPath() {
		return null;
	}
	
	default String getMigrationsPath() {
		return null;
	}
	
	default String getTemplatesPath() {
		return null;
	}
	
}
