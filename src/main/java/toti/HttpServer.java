package toti;


import java.util.List;

import ji.socketCommunication.Server;
import toti.application.Task;
import toti.registr.Register;
import toti.security.AuthenticationCache;
import ji.translator.Translator;

public class HttpServer {
	
	private final Server server;
	private final List<Task> tasks;
	private final Translator translator;
	private final Register register;
	private final AuthenticationCache sessionCache;
	
	protected  HttpServer(
			Server server, List<Task> tasks,
			Translator translator, Register register,
			AuthenticationCache sessionCache) {
		this.register = register;
		this.server = server;
		this.translator = translator;
		this.tasks = tasks;
		this.sessionCache = sessionCache;
	}
	
	public Register getRegister() {
		return register;
	}
	
	public Translator getTranslator() {
		return translator;
	}
	
	public void start() throws Exception {
		for (Task task : tasks) {
			task.start();
		}
		server.start();
		sessionCache.start();
	}
	
	public void stop() throws Exception {
		server.stop();
		sessionCache.stop();
		for (Task task : tasks) {
			task.stop();
		}
	}
	
}
