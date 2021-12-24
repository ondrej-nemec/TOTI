package toti;

import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import toti.application.Task;
import toti.registr.Registr;
import ji.translator.Translator;

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
