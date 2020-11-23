package toti;

import java.util.List;

import common.Logger;
import toti.registr.Registr;
import utils.Env;

public interface Module {

	Module initInstances(Registr registr) throws Exception;
	
	List<Task> getTasks(Env env, Logger logger) throws Exception;
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
	
	String getTranslationPath();
	
	String getMigrationsPath();
	
	String getName();
	
}
