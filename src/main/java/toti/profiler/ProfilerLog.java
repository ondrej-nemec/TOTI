package toti.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import json.Jsonable;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.profiler.HttpServerProfilerEvent;
import toti.security.Identity;

public class ProfilerLog implements Jsonable{

	private  long threadId;
	
	private final String threadName;
	
	private final Map<String, SqlLog> sqlLogs = new HashMap<>();
	
	private Map<HttpServerProfilerEvent, Long> serverEvents = new HashMap<>();
	
	private Identity identity;
	
	private HttpMethod method;
	
	private String url;
	
	private String fullUrl;
	
	private String protocol;
	
	private RequestParameters params;
	
	private long createdAt;
	
	public ProfilerLog(long threadId, String threadName) {
		this.threadId = threadId;
		this.threadName = threadName;
		this.createdAt = System.currentTimeMillis();
	}
	
	public void setRequestInfo(Identity identity, HttpMethod method, String url, String fullUrl, String protocol, RequestParameters params) {
		this.identity = identity;
		this.method = method;
		this.url = url;
		this.fullUrl = fullUrl;
		this.protocol = protocol;
		this.params = params;
	}
	
	public void addServerEvent(long time, HttpServerProfilerEvent event) {
		serverEvents.put(event, time);
	}
	
	public void addSql(String id, String sql) {
		logSql(id, (log)->{
			log.setSql(sql);
		});
	}
	
	public void addSqlParam(String id, Object param) {
		logSql(id, (log)->{
			log.addParams(param);
		});
	}
	
	public void executeSql(String id) {
		logSql(id, (log)->{
			log.setExecuted();
		});
	}

	public void setBuilder(String id, String preparedSql, String replacedSql, Map<String, String> builderParams) {
		logSql(id, (log)->{
			log.setBuilder(preparedSql, replacedSql, builderParams);
		});
	}
	
	private void logSql(String id, Consumer<SqlLog> consumer) {
		SqlLog log = sqlLogs.get(id);
		if (log == null) {
			log = new SqlLog(id);
			sqlLogs.put(id, log);
		}
		consumer.accept(log);
	}

	private long getRequestTime() {
		return serverEvents.get(HttpServerProfilerEvent.RESPONSE_SENDED) - serverEvents.get(HttpServerProfilerEvent.REQUEST_ACCEPT);
	}

	@Override
	public Object toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("id", threadId);
		json.put("name", threadName);
		json.put("method", method);
		json.put("url", url);
		json.put("fullUrl", fullUrl);
		json.put("protocol", protocol);
		json.put("params", params);
		json.put("time", getRequestTime());
		json.put("times", serverEvents);
		json.put("created", createdAt);
		json.put("locale", identity.getLocale());
		json.put("user", identity.getUser() == null ? null : identity.getUser().getId());
		json.put("allowedIds", identity.getUser() == null ? null : identity.getUser().getAllowedIds());
		// json.put("headers", identity.getHeaders());
		json.put("IP", identity.getIP());
		
		Map<String, Object> queries = new HashMap<>();
		queries.putAll(sqlLogs);
		json.put("queries", queries);
		return json;
	}
}
