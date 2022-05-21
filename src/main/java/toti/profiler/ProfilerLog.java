package toti.profiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ji.json.Jsonable;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.profiler.HttpServerProfilerEvent;
import toti.security.Identity;

public class ProfilerLog implements Jsonable{

	private  long threadId;
	
	private final String threadName;
	
	private long createdAt;
		
	/***************/
	
	private Identity identity;
	
	private Request request;
	
	private final Map<String, SqlLog> sqlLogs = new HashMap<>();
	
	private Map<HttpServerProfilerEvent, Long> serverEvents = new HashMap<>();
	
	private List<String> missingLocales = new LinkedList<>();
	
	private List<TransLog> transLog = new LinkedList<>();
	
	public ProfilerLog(long threadId, String threadName) {
		this.threadId = threadId;
		this.threadName = threadName;
		this.createdAt = System.currentTimeMillis();
	}
	
	public void setRequestInfo(Identity identity, Request request) {
		this.identity = identity;
		this.request = request;
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
	
	public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {
		transLog.add(new TransLog(locale, module, key, variables));
	}

	public void missingLocale(String locale) {
		missingLocales.add(locale);
	}

	private long getRequestTime() {
		Long first= serverEvents.get(HttpServerProfilerEvent.RESPONSE_SENDED);
		Long second = serverEvents.get(HttpServerProfilerEvent.REQUEST_ACCEPT);
		if (first == null || second == null) {
			return -1;
		}
		return first - second;
	}

	public long getThreadId() {
		return threadId;
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> requestInfo = new HashMap<>();
		Map<String, Object> iden = new HashMap<>();
		Map<String, Object> rendering = new HashMap<>();
		if (!serverEvents.isEmpty()) {
			requestInfo.put("method", request.getMethod());
			requestInfo.put("url", request.getPlainUri());
			requestInfo.put("fullUrl", request.getUri());
			requestInfo.put("protocol", request.getProtocol());
			requestInfo.put("IP", identity.getIP());
			/********/
			Map<String, Object> user = new HashMap<>();
			if (identity.isPresent()) {
				user.put("id", identity.getUser().getId());
				user.put("allowedIds", identity.getUser().getAllowedIds());
				user.put("content", identity.getUser().getContent());
				// TODO maybe serialize user
			}
			iden.put("loginMode", identity.getLoginMode());
			if (identity.isPresent()) {
				iden.put("user", user);
			}
			/********/
			rendering.put("render", getRequestTime());
			rendering.put("times", serverEvents);
		}
		
		Map<String, Object> trans = new HashMap<>();
		trans.put("locale", identity == null ? null : identity.getLocale());
		trans.put("missingFiles", missingLocales);
		trans.put("missingTranslations", transLog);
		
		Map<String, Object> queries = new HashMap<>();
		queries.putAll(sqlLogs);
		
		Map<String, Object> json = new HashMap<>();
		json.put("trans", trans);
		json.put("queries", queries);
		json.put("created", createdAt);
		json.put("name", threadName);
		
		json.put("requestInfo", requestInfo);
		json.put("params", request.getBodyInParameters()); // TODO full body
		json.put("rendering", rendering);
		json.put("identity", iden);
		
		return json;
	}
}
