package toti.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.profiler.HttpServerProfilerEvent;
import toti.security.Identity;

public class ProfilerLog {

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
	
	private void logSql(String id, Consumer<SqlLog> consumer) {
		SqlLog log = sqlLogs.get(id);
		if (log == null) {
			log = new SqlLog(id);
			sqlLogs.put(id, log);
		}
		consumer.accept(log);
	}
/*
	public Identity getIdentity() {
		return identity;
	}

	public long getThreadId() {
		return threadId;
	}

	public String getThreadName() {
		return threadName;
	}

	public Map<String, SqlLog> getSqlLogs() {
		return sqlLogs;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public String getProtocol() {
		return protocol;
	}

	public RequestParameters getParams() {
		return params;
	}
	*/
	private long getRequestTime() {
		return serverEvents.get(HttpServerProfilerEvent.REQUEST_ACCEPT) - serverEvents.get(HttpServerProfilerEvent.RESPONSE_SENDED);
	}
	
	public Map<String, Object> toMap() {
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
		json.put("locale", identity.getLocale().toString());
		json.put("user", identity.getUser() == null ? null : identity.getUser().getId());
		json.put("headers", identity.getHeaders());
		json.put("IP", identity.getIP());
		
		Map<String, Object> queries = new HashMap<>();
		json.put("queries", queries);
		sqlLogs.forEach((id, log)->{
			queries.put(id, log);
		});
		return json;
	}
}
