package toti.examples.getStarted;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toti.core.TotiServer;
import toti.core.TotiServerFactory;
import toti.examples.getStarted.core.CoreModule;

public class GetStartedMain {
	
	private final Logger logger;
	private final TotiServer server;

	public GetStartedMain() {
		this.logger = LogManager.getLogger("toti-get-started");
		TotiServerFactory serverFactory = new TotiServerFactory();
		this.server = serverFactory.create(logger);
		server.addApplication("getStarted", (env, applicationFactory)->{
			applicationFactory.setDevModeFunc(ip->true); // dev mode
			return applicationFactory.create(Arrays.asList(
				new CoreModule(logger)
			), logger);
		}, null, null);
	}

	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			logger.fatal("GetStarted:start fails", e);
		}
	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			logger.fatal("GetStarted:stp fails", e);
		}
	}


	public static void main(String[] args) {
		new GetStartedMain().start();
	}


}
