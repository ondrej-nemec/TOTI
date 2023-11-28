package toti;

import java.util.List;
import java.util.function.Consumer;

import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import toti.answers.Answer;
import toti.answers.router.Link;
import toti.application.Task;
import toti.application.register.MappedAction;
import toti.application.register.Param;
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
	private final Param root;
	
	private final String[] aliases;
	
	public Application(
			List<Task> tasks, Translator translator, Param root, Database database,
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
		this.root = root;
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
	
	public boolean isRunning() {
		return isRunning;
	}
	
	/************/
	
	
	public boolean start() throws Exception {
		if (isRunning) {
			return false;
		}
		if (database != null) {
			database.createDbIfNotExists();
			database.migrate();
		}
		for (Task task : tasks) {
			task.start();
		}
		isRunning = true;
		return true;
	}
	
	public void stop() throws Exception {
		isRunning = false;
		for (Task task : tasks) {
			task.stop();
		}
	}
	
	/****************/
	
	public void iterate(Consumer<Item> onItem) {
        iterate(onItem, root, "", 0);
    }

    private void iterate(Consumer<Item> onItem, Param param, String uri, int deep) {
        String path = getPath(uri, param);
        param.getActions().forEach((method, action)->{
             onItem.accept(new Item(path, method, action));
        });

        param.getChilds().forEach((x, child)->{
             iterate(
                 onItem,
                 child,
                 deep == 0 ? uri : path,
                 deep+1
             );
        });
    }
    
    private String getPath(String uri, Param param) {
    	if (param.getText() != null) {
            return uri + "/" + param.getText();
        }
    	return uri + "/{}";
    }

    public class Item {

        private final String uri;
        private final HttpMethod method;
        private final MappedAction action;
        
        public Item(String uri, HttpMethod method, MappedAction action) {
             this.uri = uri;
             this.method = method;
             this.action = action;
        }

        public MappedAction getAction() {
             return action;
        }

        public HttpMethod getMethod() {
             return method;
        }

        public String getUri() {
             return uri;
        }
    }
}
