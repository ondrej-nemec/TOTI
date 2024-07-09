package toti.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;

import ji.common.functions.Env;
import ji.common.structures.ThrowingBiFunction;
import toti.Application;
import toti.ApplicationFactory;

public class JettyServer {
	
	private final Server server;
	private final Logger logger;
	private final Env env;
	private final String charset;
	
//	private final Map<String, Application> applications = new HashMap<>();
	
	//private boolean isRunning = false;
	
	public JettyServer(Server server, Env env, String charset, Logger logger) {
		this.server = server;
		this.logger = logger;
		this.env = env;
		this.charset = charset;
	}

	public void start() throws Exception {
		// TODO
	}
	
	public void stop() throws Exception {
		// TODO
	}
	
}
