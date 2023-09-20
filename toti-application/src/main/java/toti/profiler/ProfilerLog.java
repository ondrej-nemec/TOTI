package toti.profiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ji.common.functions.Mapper;
import ji.common.structures.SortedMap;
import ji.common.structures.Tuple2;
import ji.json.Jsonable;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.profiler.HttpServerProfilerEvent;
import toti.application.register.MappedAction;
import toti.security.Identity;

public class ProfilerLog implements Jsonable{

	private  long threadId;
	private final String threadName;
	//private long createdAt;
		
	/***************/
	
	private Identity identity;
	private Request request;
	private MappedAction mapped;
	
	private final SortedMap<String, SqlLog> sqlLogs = new SortedMap<>();

	private List<Tuple2<HttpServerProfilerEvent, Long>> serverEvents = new LinkedList<>();
	
	private List<String> missingLocales = new LinkedList<>();
	private List<TransLog> transLog = new LinkedList<>();
	
	//private List<Tuple2<String, String>> templates = new LinkedList<>();
	private TemplateLog template;
	private final Map<Integer, TemplateLog> templateFamily = new HashMap<>();
	
	public ProfilerLog(long threadId, String threadName) {
		this.threadId = threadId;
		this.threadName = threadName;
	//	this.createdAt = System.currentTimeMillis();
	}
	
	public void setRequestInfo(Identity identity, Request request, MappedAction mapped) {
		this.identity = identity;
		this.request = request;
		this.mapped = mapped;
	}
	
	public void addServerEvent(long time, HttpServerProfilerEvent event) {
		serverEvents.add(new Tuple2<>(event, time));
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
			log.toExecute();
		});
	}
	
	public void setExecuted(String id, Object res) {
		logSql(id, (log)->{
			log.setExecuted(res);
		});
	}

	public void setBuilder(String id, String preparedSql, String replacedSql, Map<String, String> builderParams) {
		logSql(id, (log)->{
			log.setBuilder(preparedSql, replacedSql, builderParams);
		});
	}
	
	private void logSql(String id, Consumer<SqlLog> consumer) {
		SqlLog log = sqlLogs.getValue(id);
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

	public void loadFile(String locale, String domain, String path, boolean success) {
		// TODO resources are loaded only ones
	}
	
	private long getRequestTime() {
		if (serverEvents.size() != 4) {
			return -1;
		}
		return serverEvents.get(3)._2() - serverEvents.get(0)._2();
	}

	public long getThreadId() {
		return threadId;
	}
	
	public void logTemplate(String module, String path, String filename, Map<String, Object> variables,
			int parent, int self) {
		TemplateLog log = new TemplateLog(module, path, filename, variables);
		templateFamily.put(self, log);
		if (template == null) {
			this.template = log;
		} else if (parent == 0) {
			// ignore
		} else {
			templateFamily.get(parent).addChild(log);
		}
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> json = new HashMap<>();
		if (mapped != null) {
			Map<String, Object> controller = new HashMap<>();
			controller.put("module", mapped.getModuleName());
			controller.put("class", mapped.getClassName());
			controller.put("method", mapped.getMethodName());
			controller.put("authMode", mapped.getSecurityMode());

			json.put("controller", controller);
		}
		if (request != null) {
			Map<String, Object> requestInfo = new HashMap<>();
			requestInfo.put("method", request.getMethod());
			requestInfo.put("url", request.getPlainUri());
			requestInfo.put("processTime", getRequestTime());
			requestInfo.put("loginMode", identity.getLoginMode());
			requestInfo.put("params", request.getQueryParameters());
			requestInfo.put("locale", Mapper.get().serialize(identity.getLocale()));

			json.put("request", requestInfo);
			
			json.put("title", request.getMethod() + " " + request.getPlainUri());
		} else {
			json.put("title", threadName + "(" + threadId + ")");
		}
		
		if (template != null) {
			json.put("template", template);
		}
		
		Map<String, Object> trans = new HashMap<>();
		trans.put("missingFiles", missingLocales);
		trans.put("missingTranslations", transLog);
		
		json.put("translations", trans);
		
		json.put("queries", sqlLogs);
		
		Map<String, Object> user = new HashMap<>();
		if (identity != null && identity.isPresent()) {
			user.put("id", identity.getUser().getId());
			user.put("allowedIds", identity.getUser().getAllowedIds());
			user.put("content", identity.getUser().getContent());

			json.put("user", user);
		}
		return json;
	}
	
	@Override
	public String toString() {
		return threadName + ": " + threadId;
	}
}
