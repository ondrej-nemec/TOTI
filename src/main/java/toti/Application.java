package toti;

import java.util.List;

import ji.database.Database;
import toti.application.Task;
import toti.register.Register;
import toti.security.AuthenticationCache;
import toti.url.Link;
import ji.translator.Translator;

public class Application {
	
	
	private final List<Task> tasks;
	private final AuthenticationCache sessionCache;
	
	private final Translator translator;
	private final Database database;
	private final Link link;
	private final Register register;
	private final List<String> migrations;
	// Map<String, TemplateFactory> templateFactories
	private final ResponseFactory responseFactory;
	
	private final boolean autoStart;
	private boolean isRunning = false;
	
	public Application(
			List<Task> tasks, AuthenticationCache sessionCache, Translator translator, Database database,
			Link link, Register register, List<String> migrations, ResponseFactory responseFactory,
			boolean autoStart) {
		this.tasks = tasks;
		this.sessionCache = sessionCache;
		this.translator = translator;
		this.database = database;
		this.link = link;
		this.register = register;
		this.migrations = migrations;
		this.responseFactory = responseFactory;
		this.autoStart = autoStart;
	}

/*
	public List<Task> getTasks() {
		return tasks;
	}

	public AuthenticationCache getSessionCache() {
		return sessionCache;
	}
*/

	public Translator getTranslator() {
		return translator;
	}

	public Database getDatabase() {
		return database;
	}

	public Link getLink() {
		return link;
	}

	public Register getRegister() {
		return register;
	}

	public List<String> getMigrations() {
		return migrations;
	}

	protected ResponseFactory getResponseFactory() {
		return responseFactory;
	}

	public boolean isAutoStart() {
		return autoStart;
	}
	
	/************/
	
	
	public void start() throws Exception {
		if (isRunning) {
			return;
		}
		if (database != null) {
			database.createDbIfNotExists();
			database.migrate();
		}
		for (Task task : tasks) {
			task.start();
		}
		sessionCache.start();
		isRunning = true;
	}
	
	public void stop() throws Exception {
		isRunning = false;
		for (Task task : tasks) {
			task.stop();
		}
		sessionCache.stop();
	}
	
}
