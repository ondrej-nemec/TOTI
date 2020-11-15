package toti;

import toti.registr.Registr;

public interface ModuleConfig {

	void initInstances(Registr registr);
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
}
