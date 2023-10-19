package toti;

import java.util.List;

import ji.database.Database;
import toti.answers.Answer;
import toti.answers.router.Link;
import toti.application.Task;
import toti.application.register.Register;
import toti.security.AuthenticationCache;
import toti.security.Authenticator;
import toti.security.Authorizator;
import ji.translator.Translator;

public class Application {
	
	
	private final List<Task> tasks;
	private final AuthenticationCache sessionCache;
	
	private final Translator translator;
	private final Database database;
	private final Link link;
	private final Register register;
	private final List<String> migrations;
	private final Authenticator authenticator;
    private final Authorizator authorizator;
	// Map<String, TemplateFactory> templateFactories
	private final Answer answer;
	
	private final boolean autoStart;
	private boolean isRunning = false;
	
	private final String[] aliases;
	
	public Application(
			List<Task> tasks, AuthenticationCache sessionCache, Translator translator, Database database,
			Link link, Register register, List<String> migrations, Answer answer,
			Authenticator authenticator, Authorizator authorizator,
			boolean autoStart, String... aliases) {
		this.tasks = tasks;
		this.sessionCache = sessionCache;
		this.translator = translator;
		this.database = database;
		this.link = link;
		this.register = register;
		this.migrations = migrations;
		this.answer = answer;
		this.authenticator = authenticator;
		this.authorizator = authorizator;
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
	
	public Authenticator getAuthenticator() {
		return authenticator;
	}
	
	public Authorizator getAuthorizator() {
		return authorizator;
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
