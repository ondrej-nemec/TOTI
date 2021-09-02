package toti.profiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import common.structures.MapInit;
import database.support.SqlQueryProfiler;
import translator.TransProfiler;
import json.Jsonable;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.profiler.HttpServerProfiler;
import socketCommunication.http.server.profiler.HttpServerProfilerEvent;
import toti.security.Identity;

public class Profiler implements TransProfiler, HttpServerProfiler, SqlQueryProfiler,  Jsonable {

	private final Map<Object, ProfilerLog> logByThread = new HashMap<>();
	// private final List<Object> notPageIds = new LinkedList<>();
	private final Map<Object, ProfilerLog> notPageLog = new HashMap<>();
	private final Map<String, List<ProfilerLog>> logByPage = new HashMap<>();
	
	/****************/
	
	public void setPageId(String id) {
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
	
	/***********/
	
	public void logRequest(Identity identity, HttpMethod method, String url, String fullUrl, String protocol, RequestParameters params) {
		log((log)->{
			log.setRequestInfo(identity, method, url, fullUrl, protocol, params);
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
	
	/**************/

	private void log(Consumer<ProfilerLog> consumer) {
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
	public void log(HttpServerProfilerEvent event) {
		long threadId = Thread.currentThread().getId();
		notPageLog.remove(threadId);
		ProfilerLog log = logByThread.get(threadId);
		if (log != null && event == HttpServerProfilerEvent.REQUEST_ACCEPT) {
			logByThread.put(
				threadId + "_" + new Random().nextDouble(),
				logByThread.remove(threadId)
			);
			log = null;
		}
		if (log == null) {
			log = new ProfilerLog(threadId, Thread.currentThread().getName());
			logByThread.put(threadId, log);
		}
		log.addServerEvent(System.currentTimeMillis(), event);
	}

	@Override
	public Object toJson() {
		return new MapInit<String, Object>()
		.append("logByPage", logByPage)
		.append("noPageLog", notPageLog)
		.toMap();
	}
	
	public List<ProfilerLog> getProfilerForPage(String id) {
		return logByPage.get(id);
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
/*
	private List<Object> getPageLogs(List<ProfilerLog> logs) {
		List<Object> json = new LinkedList<>();
		logs.forEach((log)->{
			json.add(log);
		});
		return json;
	}
*/
}
