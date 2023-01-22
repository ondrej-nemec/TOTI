package toti.profiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import toti.response.Response;
import toti.security.Identity;
import toti.templating.TemplateProfiler;
import toti.url.MappedUrl;

public class Profiler implements TransProfiler, HttpServerProfiler, SqlQueryProfiler, TemplateProfiler, Jsonable {

	private final Map<Object, ProfilerLog> logByThread = new HashMap<>();
	// private final List<Object> notPageIds = new LinkedList<>();
	private final Map<Object, ProfilerLog> notPageLog = new HashMap<>();
	private final Map<String, List<ProfilerLog>> logByPage = new HashMap<>();
	
	private boolean use = false;
	private boolean enable = false;
	
	/****************/
	
	public void setPageId(String id) {
		if (!enable) {
			return;
		}
		List<ProfilerLog> logs = logByPage.get(id);
		if (logs == null) {
			logs = new LinkedList<>();
			logByPage.put(id, logs);
		}
		logs.add(logByThread.get(Thread.currentThread().getId()));
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

	@Override
	public void executed(String identifier, Object res) {
		log((log)->{
			log.executeSql(identifier, res);
		});
	}

	
	/***********/
	
	public void logRequest(Identity identity, Request request, MappedUrl mapped) {
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
	
	/*******************************/

	@Override
	public void logGetTemplate(String module, String filename) {
		log((log)->{
			log.logTemplate(module, filename);
		});
	}
	
	/**************/

	private void log(Consumer<ProfilerLog> consumer) {
		if (!enable) {
			return;
		}
		ProfilerLog log = logByThread.get(Thread.currentThread().getId());
		if (log != null) {
			consumer.accept(log);
		} else {
			long threadId = Thread.currentThread().getId();
			log = new ProfilerLog(threadId, Thread.currentThread().getName());
			logByThread.put(threadId, log);
			notPageLog.put(threadId, log);
		}
	}
	
	/**********/
	
	@Override
	public void log(Map<HttpServerProfilerEvent, Long> events) {
		if (!enable) {
			return;
		}
		long threadId = Thread.currentThread().getId();
		notPageLog.remove(threadId);
		ProfilerLog log = logByThread.get(threadId);
		/*if (log != null && event == HttpServerProfilerEvent.REQUEST_ACCEPT) {
			logByThread.put(
				threadId + "_" + new Random().nextDouble(),
				logByThread.remove(threadId)
			);
			log = null;
		}*/
		if (log == null) {
			log = new ProfilerLog(threadId, Thread.currentThread().getName());
			logByThread.put(threadId, log);
		}
		for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
			log.addServerEvent(events.get(event), event);
		}
		
	}

	@Override
	public Object toJson() {
		return new MapInit<String, Object>()
		.append("logByPage", logByPage)
		.append("noPageLog", notPageLog)
		.toMap();
	}

	public void clearProfilerForPage(String id) {
		List<ProfilerLog> logs = logByPage.remove(id);
		logs.forEach((log)->{
			logByThread.remove(log.getThreadId());
		});
	}
	
	public void clearProfilerWithoutPage(long id) {
		notPageLog.remove(id);
		logByThread.remove(id);
	}
	
	public void clear() {
		notPageLog.clear();
		logByThread.clear();
		logByPage.clear();
	}

	public Response getResponse(HttpMethod method, RequestParameters params) {
		switch (method) {
			case GET: 
				return Response.getTemplate(
					"/profiler.jsp",
					new MapInit<String, Object>("enable", enable).toMap()
				);
			case DELETE:
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
			case PATCH:
			default:
				return Response.getText(StatusCode.NOT_FOUND, "");
		}
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
		this.enable = use;
	}
	
}
