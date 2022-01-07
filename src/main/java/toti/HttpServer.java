package toti;


import java.util.List;

import ji.socketCommunication.Server;
import toti.application.Task;
import toti.registr.Register;
import ji.translator.Translator;

public class HttpServer {
	
	private final Server server;
	private final List<Task> tasks;
	private final Translator translator;
	private final Register register;
	
	protected  HttpServer(Server server, List<Task> tasks, Translator translator, Register register) {
		this.register = register;
		this.server = server;
		this.translator = translator;
		this.tasks = tasks;
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
	}
	
	public void stop() throws Exception {
		server.stop();
		for (Task task : tasks) {
			task.stop();
		}
	}
	
}
