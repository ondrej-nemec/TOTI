package samples.getstarted.init;

import java.util.Arrays;

import toti.Application;

public class QuickInit {
	
	public static void main(String[] args) {
		// Application.APP_CONFIG_FILE = "";
		Application application = new Application(Arrays.asList(
			// modules
		));
		
		// if start fail, System.exit is called
		// start() IS NOT BLOCKING
		application.start();
		
		// if stop fail, System.exit is called
		application.stop();
	}
	
}
