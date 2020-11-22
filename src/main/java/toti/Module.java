package toti;

import toti.registr.Registr;

public interface Module {

	Module initInstances(Registr registr) throws Exception;
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
	
	String getName();
	
}
