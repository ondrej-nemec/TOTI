package adminer;

import toti.ModuleConfig;
import toti.Router;
import toti.registr.Registr;

public class AdminerModule implements ModuleConfig {

	@Override
	public void initInstances(Registr registr) {
		registr.addFactory(Adminer.class, ()->{
			return new Adminer();
		});
	}

	@Override
	public void addRoutes(Router router) {
		
	}

	@Override
	public String getTemplatesPath() {
		return "templates";
	}

	@Override
	public String getControllersPath() {
		return "adminer";
	}

}
