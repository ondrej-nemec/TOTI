package toti;

import java.util.List;

import common.Logger;
import common.functions.Env;
import database.Database;
import toti.application.Task;
import toti.registr.Registr;

public interface Module {

	List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception;
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
	
	String getTranslationPath();
	
	String getMigrationsPath();
	
	String getName();
	
}
