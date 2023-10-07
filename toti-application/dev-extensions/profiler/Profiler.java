package toti.profiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import ji.common.structures.MapInit;
import ji.database.support.SqlQueryProfiler;
import ji.translator.TransProfiler;
import ji.json.Jsonable;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.profiler.HttpServerProfiler;
import ji.socketCommunication.http.profiler.HttpServerProfilerEvent;
import toti.answers.response.Response;
import toti.application.register.MappedAction;
import toti.security.Identity;
import toti.templating.TemplateProfiler;

public class Profiler implements TransProfiler, HttpServerProfiler, SqlQueryProfiler, TemplateProfiler, Jsonable {

	private final ConcurrentMap<Long, ProfilerLog> logByThread = new ConcurrentHashMap<>();
	// private final List<Object> notPageIds = new LinkedList<>();
	private final ConcurrentMap<Long, ProfilerLog> notPageLog = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, List<ProfilerLog>> logByPage = new ConcurrentHashMap<>();
	
	private boolean use = false;
	
	public void startProfilePage(String id) {
		if (!use) {
			return;
		}
		// long threadId = Thread.currentThread().getId();
		// pages.put(threadId, new ProfilerLog(threadId, Thread.currentThread().getName()));

		Long threadId = Thread.currentThread().getId();
		List<ProfilerLog> logs = logByPage.get(id);
		if (logs == null) {
			logs = new LinkedList<>();
			logByPage.put(id, logs);
		}
		if (logByThread.containsKey(threadId)) {
			logs.add(logByThread.get(threadId));
		} else {
			logs.add(createNewLog(threadId));
		}
		notPageLog.remove(threadId);
	}
	
	/****************/
	
	@Override
	public void execute(String identifier, String sql) {
		log((log)->{
			log.addSql(identifier, sql);
			log.executeSql(identifier);
		});
	}

	@Override
	public void execute(String identifier) {
		log((log)->{
			log.executeSql(identifier);
		});
	}

	@Override
	public void executed(String identifier, Object res) {
		log((log)->{
			log.setExecuted(identifier, res);
		});
	}
	@Override
	public void prepare(String identifier, String sql) {
		log((log)->{
			log.addSql(identifier, sql);
		});
	}

	@Override
	public void addParam(String identifier, Object param) {
		log((log)->{
			log.addSqlParam(identifier, param);
		});
	}

	@Override
	public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {
		log((log)->{
			log.setBuilder(identifier, query, sql, params);
		});
	}

	
	/***********/
	
	public void logRequest(Identity identity, Request request, MappedAction mapped) {
		log((log)->{
			log.setRequestInfo(identity, request, mapped);
		});
	}
	
	/***********/

	@Override
	public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {
		log((log)->{
			log.missingParameter(module, key, variables, locale);
		});
	}

	@Override
	public void missingLocale(String locale) {
		log((log)->{
			log.missingLocale(locale);
		});
	}
	
	@Override
	public void loadFile(String locale, String domain, String path, boolean success) {
		log((log)->{
			log.loadFile(locale, domain, path, success);
		});
	}
	
	/*******************************/

	@Override
	public void logGetTemplate(
			String module, String namespace, String filename, Map<String, Object> variables, int parent, int self) {
		log((log)->{
			log.logTemplate(module, namespace, filename, variables, parent, self);
		});
	}
	
	@Override
	public void log(Map<HttpServerProfilerEvent, Long> events) {
		Long id = Thread.currentThread().getId();
		if (!logByThread.containsKey(id)) {
			return; // events are logged as last
		}
		log((log)->{
			for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
				log.addServerEvent(events.get(event), event);
			}
		});
	}
	
	/**************/

	private void log(Consumer<ProfilerLog> consumer) {
		if (!use) {
			return;
		}
		Long id = Thread.currentThread().getId();
		if (!logByThread.containsKey(id)) {
			ProfilerLog log = createNewLog(id);
			notPageLog.put(id, log);
		}
		consumer.accept(logByThread.get(id));
	}
	
	private ProfilerLog createNewLog(Long id) {
		ProfilerLog log = new ProfilerLog(id, Thread.currentThread().getName());
		logByThread.put(id, log);
		return log;
	}

	public Response getResponse(HttpMethod method, RequestParameters params) {
		switch (method) {
			case POST:
				String id = params.getString("pageId");
				if (id == null) {
					return Response.getJson(new LinkedList<>(notPageLog.values()));
				}
				if (!logByPage.containsKey(id)) {
					return Response.getText(StatusCode.NOT_FOUND, "Not existing pageId");
				}
				return Response.getJson(new LinkedList<>(logByPage.get(id)));
			case GET: 
				return Response.getTemplate("/profiler.jsp", new MapInit<String, Object>().toMap());
			/*case DELETE:
				if (params.containsKey("id") && params.is("id", Long.class)) {
					clearProfilerWithoutPage(params.getLong("id"));
				} else if (params.containsKey("id")) {
					clearProfilerForPage(params.getString("id"));
				} else {
					clear();
				}
				return Response.getText("OK");
			case PUT:
				this.enable = !enable;
				return Response.getText("OK");
			case POST:
				if (params.containsKey("id")) {
					return Response.getJson(logByPage.get(params.getString("id")));
				}
				return Response.getJson(toJson());
			case PATCH:*/
			default:
				return Response.getText(StatusCode.NOT_FOUND, "");
		}
	}

	public boolean isUse() {
		return use;
	}
	
	public Profiler used() {
		if (use) {
			return this;
		}
		return null;
	}

	public void setUse(boolean use) {
		this.use = use;
		//this.enable = use;
	}
	
}
