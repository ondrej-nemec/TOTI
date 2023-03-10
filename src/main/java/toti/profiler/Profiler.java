package toti.profiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import ji.database.support.SqlQueryProfiler;
import ji.files.text.Text;
import ji.translator.TransProfiler;
import ji.json.InputJsonStream;
import ji.json.JsonReader;
import ji.json.JsonStreamException;
import ji.json.JsonWritter;
import ji.json.Jsonable;
import ji.json.providers.InputReaderProvider;
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

	//private final Map<Object, ProfilerLog> logByThread = new HashMap<>();
	// private final List<Object> notPageIds = new LinkedList<>();
	//private final Map<Object, ProfilerLog> notPageLog = new HashMap<>();
	//private final Map<String, List<ProfilerLog>> logByPage = new HashMap<>();
	
	private boolean use = false;
	private final ConcurrentMap<Long, ProfilerLog> pages = new ConcurrentHashMap<>();
	//private boolean enable = false;
	
	//private final Map<String, Object> a = new HashMap<>();
	// k threadid - dany log
	// logovat jen kdyz page id
	
	/****************/
	
	public ProfilerLog getPage(Long id) {
		return pages.remove(id);
	}
	
	public String getCurrentPageId() {
		if (!use) {
			return null;
		}
		return Thread.currentThread().getId() + "";
	}
	
	public void startProfilePage() {
		if (!use) {
			return;
		}
		if (pages.size() > 0) {
			pages.keySet().forEach((pageId)->{
				ProfilerLog last = getPage(pageId);
				JsonWritter writter = new JsonWritter();
				try {
					String filename = "profiler/" + pageId + ".log";
					Text.get().write((br)->{
						br.write(writter.write(last.toJson()));
					}, filename, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		
		long threadId = Thread.currentThread().getId();
		pages.put(threadId, new ProfilerLog(threadId, Thread.currentThread().getName()));
	}
	/*
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
		if (!use) {
			return;
		}
		log((log)->{
			log.addSql(identifier, sql);
			log.executeSql(identifier);
		});
	}

	@Override
	public void execute(String identifier) {
		if (!use) {
			return;
		}
		log((log)->{
			log.executeSql(identifier);
		});
	}

	@Override
	public void executed(String identifier, Object res) {
		if (!use) {
			return;
		}
		log((log)->{
			log.setExecuted(identifier, res);
		});
	}
	@Override
	public void prepare(String identifier, String sql) {
		if (!use) {
			return;
		}
		log((log)->{
			log.addSql(identifier, sql);
		});
	}

	@Override
	public void addParam(String identifier, Object param) {
		if (!use) {
			return;
		}
		log((log)->{
			log.addSqlParam(identifier, param);
		});
	}

	@Override
	public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {
		if (!use) {
			return;
		}
		log((log)->{
			log.setBuilder(identifier, query, sql, params);
		});
	}

	
	/***********/
	
	public void logRequest(Identity identity, Request request, MappedUrl mapped) {
		if (!use) {
			return;
		}
		log((log)->{
			log.setRequestInfo(identity, request, mapped);
		});
	}
	
	/***********/

	@Override
	public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {
		if (!use) {
			return;
		}
		log((log)->{
			log.missingParameter(module, key, variables, locale);
		});
	}

	@Override
	public void missingLocale(String locale) {
		if (!use) {
			return;
		}
		log((log)->{
			log.missingLocale(locale);
		});
	}
	
	@Override
	public void loadFile(String locale, String domain, String path, boolean success) {
		if (!use) {
			return;
		}
		log((log)->{
			log.loadFile(locale, domain, path, success);
		});
	}
	
	/*******************************/

	@Override
	public void logGetTemplate(
			String module, String namespace, String filename, Map<String, Object> variables,
			int parent, int self) {
		if (!use) {
			return;
		}
		log((log)->{
			log.logTemplate(module, namespace, filename, variables, parent, self);
		});
	}
	
	@Override
	public void log(Map<HttpServerProfilerEvent, Long> events) {
		if (!use) {
			return;
		}
		log((log)->{
			for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
				log.addServerEvent(events.get(event), event);
			}
		});
	}
	
	/**************/

	private void log(Consumer<ProfilerLog> consumer) {
		Long id = Thread.currentThread().getId();
		if (!use || !pages.containsKey(id)) {
			return;
		}
		consumer.accept(pages.get(id));
	}
	
	/**********/
/*
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
	}*/

	public Response getResponse(HttpMethod method, RequestParameters params) {
		switch (method) {
			case POST:
		/*		List<Object> response = new LinkedList<>();
			try {
				pages.values();
				Object o = new JsonReader()
				.read(new InputJsonStream(new InputReaderProvider(new FileReader("profiler/18.log"))));
				response.add(o);
				response.add(o);
				response.add(o);
				response.add(o);
				response.add(o);
				response.add(o);
				response.add(o);
				response.add(o);
			} catch (JsonStreamException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return Response.getJson(pages.values());
			// return Response.getFile("profiler/18.log");
			/*case GET: 
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
