package toti;

import java.util.List;

import ji.database.Database;
import toti.answers.Answer;
import toti.answers.router.Link;
import toti.application.Task;
import toti.application.register.Register;
import ji.translator.Translator;

public class Application {
	
	
	private final List<Task> tasks;
	
	private final Translator translator;
	//private final SessionUserProvider sessionUserProvider;
	private final Database database;
	private final Link link;
	private final Register register;
	private final List<String> migrations;
	// Map<String, TemplateFactory> templateFactories
	private final Answer answer;
	
	private final boolean autoStart;
	private boolean isRunning = false;
	
	private final String[] aliases;
	
	public Application(
			List<Task> tasks, /*SessionUserProvider sessionUserProvider, */Translator translator, Database database,
			Link link, Register register, List<String> migrations, Answer answer,
			boolean autoStart, String... aliases) {
		this.tasks = tasks;
		this.translator = translator;
		this.database = database;
		this.link = link;
		this.register = register;
		this.migrations = migrations;
		this.answer = answer;
		//this.sessionUserProvider = sessionUserProvider;
		this.autoStart = autoStart;
		this.aliases = aliases;
	}

/*
	public List<Task> getTasks() {
		return tasks;
	}

	public AuthenticationCache getSessionCache() {
		return sessionCache;
	}
*/
	
	public String[] getAliases() {
		return aliases;
	}

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

	protected Answer getRequestAnswer() {
		return answer;
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
		isRunning = true;
	}
	
	public void stop() throws Exception {
		isRunning = false;
		for (Task task : tasks) {
			task.stop();
		}
	}
	
}
