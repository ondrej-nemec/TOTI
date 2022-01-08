package samples.getstarted.init;

import java.util.Arrays;

import toti.Application;

public class QuickInit {
	
	public static void main(String[] args) {
		Application.APP_CONFIG_FILE = "samples/getstarted/init/quickInit.properties";
		Application application = new Application(Arrays.asList(
			// modules
		));
		
		// if start fail, System.exit is called
		// start() IS NOT BLOCKING
		application.start();
		
		// http://localhost:8080/toti
		
		// sleep for 2min before automatic close
		try { Thread.sleep(2 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
		
		// if stop fail, System.exit is called
		application.stop();
	}
	
}
