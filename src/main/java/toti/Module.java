package toti;

import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import toti.application.Task;
import toti.registr.Register;
import ji.translator.Translator;

public interface Module {
	
	String getName();
	
	String getControllersPath();

	List<Task> initInstances(Env env, Translator translator, Register registr, Database database, Logger logger) throws Exception;
	
	default void addRoutes(Router router) {}
	
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
