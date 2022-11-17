package toti.tutorial1;

import java.util.Arrays;
import java.util.List;

import toti.Application;
import toti.Module;

public class EdgeDevicesControl {
	
	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(
			new EdgeControlModule()
		);
		Application.APP_CONFIG_FILE = "examples/tutorial1/resources/conf/app.properties";
		try {
			Application app = new Application(modules);
			app.start();
			
			// for security reason - automatic stop after 2 hours
			try { Thread.sleep(2 * 3600 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			app.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
