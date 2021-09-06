package toti;

import java.util.List;

import common.Logger;
import common.functions.Env;
import database.Database;
import toti.application.Task;
import toti.registr.Registr;
import translator.Translator;

public interface Module {
	
	String getName();
	
	String getControllersPath();

	@Deprecated
	List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception;

	default List<Task> initInstances(Env env, Translator translator, Registr registr, Database database, Logger logger) throws Exception {
		// TODO remove default
		return initInstances(env, registr, database, logger);
	}
	
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
