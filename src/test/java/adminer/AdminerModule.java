package adminer;

import toti.Module;
import toti.Router;
import toti.registr.Registr;

public class AdminerModule implements Module {

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

	@Override
	public String getName() {
		return "adminer";
	}

}
