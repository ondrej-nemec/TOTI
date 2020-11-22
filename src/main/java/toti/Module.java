package toti;

import toti.registr.Registr;

public interface Module {

	void initInstances(Registr registr) throws Exception;
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
	
	String getName();
	
}
