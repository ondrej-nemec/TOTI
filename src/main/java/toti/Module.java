package toti;

import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import toti.application.Task;
import toti.register.Register;
import toti.url.Link;
import ji.translator.Translator;

public interface Module {
	
	String getName();
	
	default String getPath() {
		return getName();
	}
	
	String getControllersPath();

	@Deprecated
	List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger) throws Exception;
	
	default List<Task> initInstances(
		Env env, Translator translator, Register register,
		Link link, Database database, Logger logger
	) throws Exception {
		return initInstances(env, translator, register, database, logger);
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
