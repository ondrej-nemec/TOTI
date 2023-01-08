package toti.tutorial1;

import java.util.Arrays;

import toti.HttpServer;
import toti.HttpServerFactory;

public class EdgeDevicesControl {
	
	public static void main(String[] args) {
		try {
			HttpServer server = new HttpServerFactory("examples/tutorial1/resources/conf/app.properties").create();
			server.addApplication("localhost", (env, applicationFactory)->{
				return applicationFactory.create(Arrays.asList(
					new EdgeControlModule()
				));
			});
			server.start();
			
			// for security reason - automatic stop after 2 hours
			try { Thread.sleep(2 * 3600 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
