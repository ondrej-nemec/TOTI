package toti;

import toti.registr.Registr;

public interface Module {

	void initInstances(Registr registr);
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
	
	String getModuleName();
	
}
