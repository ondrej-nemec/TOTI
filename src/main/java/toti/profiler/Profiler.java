package toti.profiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ji.common.functions.StackTrace;
import ji.common.structures.MapInit;
import ji.database.support.SqlQueryProfiler;
import ji.files.text.Text;
import ji.translator.TransProfiler;
import ji.json.JsonWritter;
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
					}, filename, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		
		long threadId = Thread.currentThread().getId();
		pages.put(threadId,  new ProfilerLog(threadId, Thread.currentThread().getName()));
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
		//*
		log((log)->{
			log.addSql(identifier, sql);
			log.executeSql(identifier);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier + " EXECUTE: " + sql);
		//*/
	}

	@Override
	public void execute(String identifier) {
		//*
		log((log)->{
			log.executeSql(identifier);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier + " EXECUTE");
		//*/
	}

	@Override
	public void executed(String identifier, Object res) {
		//*
		log((log)->{
			log.setExecuted(identifier, res);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier + " EXECUTED: " + res);
		//*/
	}
	@Override
	public void prepare(String identifier, String sql) {
		//*
		log((log)->{
			log.addSql(identifier, sql);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier + " PREPARE: " + sql);
		//*/
	}

	@Override
	public void addParam(String identifier, Object param) {
		//*
		log((log)->{
			log.addSqlParam(identifier, param);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier + " ADD PARAM: " + param);
		//*/
	}

	@Override
	public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {
		//*
		log((log)->{
			log.setBuilder(identifier, query, sql, params);
		});
		/*/
		if (!use) {
			return;
		}
		log("SQL " + identifier +  " QUERY BUILDER: " + query + " " + sql + " " + params);
		//*/
	}

	
	/***********/
	
	public void logRequest(Identity identity, Request request, MappedUrl mapped) {
		//*
		log((log)->{
			log.setRequestInfo(identity, request, mapped);
		});
		/*/
		if (!use) {
			return;
		}
		log("REQUEST IDENTITY: " + identity);
		log("REQUEST REQUEST: " + request);
		log("REQUEST MAPPED: " + mapped);
		//*/
	}
	
	/***********/

	@Override
	public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {
		//*
		log((log)->{
			log.missingParameter(module, key, variables, locale);
		});
		/*/
		if (!use) {
			return;
		}
		log("TRANS MISSING PARAMETER: " + module + " " + locale + " " + variables);
		//*/
	}

	@Override
	public void missingLocale(String locale) {
		//*
		log((log)->{
			log.missingLocale(locale);
		});
		/*/
		if (!use) {
			return;
		}
		log("TRANS MISSING LOCALE: " + locale);
		//*/
	}
	
	/*******************************/

	@Override
	public void logGetTemplate(String module, String filename) {
		//*
		/*
		System.err.println("LOG template " + module + " " + filename);
		for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
			System.err.println(" -> " + el);
		}
		/*
		LOG template tools-examples examples/samples/templates/developtools/index.jsp
 -> java.base/java.lang.Thread.getStackTrace(Thread.java:1602)
 -> toti.profiler.Profiler.logGetTemplate(Profiler.java:235)
 -> toti.templating.TemplateFactory.getTemplateWithAbsolutePath(TemplateFactory.java:133)
 -> toti.templating.TemplateFactory.getTemplate(TemplateFactory.java:112)
 -> toti.response.TemplateResponse.createResponse(TemplateResponse.java:78)
 -> toti.response.TemplateResponse.getResponse(TemplateResponse.java:68)
 -> toti.ResponseFactory.getControllerResponse(ResponseFactory.java:323)
 -> toti.ResponseFactory.getMappedResponse(ResponseFactory.java:222)
 -> toti.ResponseFactory.getRoutedResponse(ResponseFactory.java:176)
 -> toti.ResponseFactory.getTotiFilteredResponse(ResponseFactory.java:166)
 -> toti.ResponseFactory.getNormalizedResponse(ResponseFactory.java:150)
 -> toti.ResponseFactory.getCatchedResponse(ResponseFactory.java:126)
 -> toti.ResponseFactory.accept(ResponseFactory.java:120)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:101)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:42)
 -> ji.socketCommunication.Server.lambda$1(Server.java:192)
 -> java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
 -> java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
 -> java.base/java.lang.Thread.run(Thread.java:835)
LOG template tools-examples examples/samples/templates/developtools/layout.jsp
 -> java.base/java.lang.Thread.getStackTrace(Thread.java:1602)
 -> toti.profiler.Profiler.logGetTemplate(Profiler.java:235)
 -> toti.templating.TemplateFactory.getTemplateWithAbsolutePath(TemplateFactory.java:133)
 -> toti.templating.TemplateFactory.getTemplate(TemplateFactory.java:112)
 -> examples_samples_templates_developtools.index._create(index.java:1)
 -> toti.templating.Template.create(Template.java:23)
 -> toti.response.TemplateResponse.createResponse(TemplateResponse.java:79)
 -> toti.response.TemplateResponse.getResponse(TemplateResponse.java:68)
 -> toti.ResponseFactory.getControllerResponse(ResponseFactory.java:323)
 -> toti.ResponseFactory.getMappedResponse(ResponseFactory.java:222)
 -> toti.ResponseFactory.getRoutedResponse(ResponseFactory.java:176)
 -> toti.ResponseFactory.getTotiFilteredResponse(ResponseFactory.java:166)
 -> toti.ResponseFactory.getNormalizedResponse(ResponseFactory.java:150)
 -> toti.ResponseFactory.getCatchedResponse(ResponseFactory.java:126)
 -> toti.ResponseFactory.accept(ResponseFactory.java:120)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:101)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:42)
 -> ji.socketCommunication.Server.lambda$1(Server.java:192)
 -> java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
 -> java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
 -> java.base/java.lang.Thread.run(Thread.java:835)
LOG template tools-examples examples/samples/templates/developtools/included.jsp
 -> java.base/java.lang.Thread.getStackTrace(Thread.java:1602)
 -> toti.profiler.Profiler.logGetTemplate(Profiler.java:235)
 -> toti.templating.TemplateFactory.getTemplateWithAbsolutePath(TemplateFactory.java:133)
 -> toti.templating.TemplateFactory.getTemplate(TemplateFactory.java:112)
 -> examples_samples_templates_developtools.index.lambda$_create$1(index.java:1)
 -> examples_samples_templates_developtools.layout._create(layout.java:1)
 -> examples_samples_templates_developtools.index._create(index.java:1)
 -> toti.templating.Template.create(Template.java:23)
 -> toti.response.TemplateResponse.createResponse(TemplateResponse.java:79)
 -> toti.response.TemplateResponse.getResponse(TemplateResponse.java:68)
 -> toti.ResponseFactory.getControllerResponse(ResponseFactory.java:323)
 -> toti.ResponseFactory.getMappedResponse(ResponseFactory.java:222)
 -> toti.ResponseFactory.getRoutedResponse(ResponseFactory.java:176)
 -> toti.ResponseFactory.getTotiFilteredResponse(ResponseFactory.java:166)
 -> toti.ResponseFactory.getNormalizedResponse(ResponseFactory.java:150)
 -> toti.ResponseFactory.getCatchedResponse(ResponseFactory.java:126)
 -> toti.ResponseFactory.accept(ResponseFactory.java:120)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:101)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:42)
 -> ji.socketCommunication.Server.lambda$1(Server.java:192)
 -> java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
 -> java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
 -> java.base/java.lang.Thread.run(Thread.java:835)
LOG template tools-examples examples/samples/templates/developtools/included.jsp
 -> java.base/java.lang.Thread.getStackTrace(Thread.java:1602)
 -> toti.profiler.Profiler.logGetTemplate(Profiler.java:235)
 -> toti.templating.TemplateFactory.getTemplateWithAbsolutePath(TemplateFactory.java:133)
 -> toti.templating.TemplateFactory.getTemplate(TemplateFactory.java:112)
 -> examples_samples_templates_developtools.index.lambda$_create$1(index.java:1)
 -> examples_samples_templates_developtools.layout._create(layout.java:1)
 -> examples_samples_templates_developtools.index._create(index.java:1)
 -> toti.templating.Template.create(Template.java:23)
 -> toti.response.TemplateResponse.createResponse(TemplateResponse.java:79)
 -> toti.response.TemplateResponse.getResponse(TemplateResponse.java:68)
 -> toti.ResponseFactory.getControllerResponse(ResponseFactory.java:323)
 -> toti.ResponseFactory.getMappedResponse(ResponseFactory.java:222)
 -> toti.ResponseFactory.getRoutedResponse(ResponseFactory.java:176)
 -> toti.ResponseFactory.getTotiFilteredResponse(ResponseFactory.java:166)
 -> toti.ResponseFactory.getNormalizedResponse(ResponseFactory.java:150)
 -> toti.ResponseFactory.getCatchedResponse(ResponseFactory.java:126)
 -> toti.ResponseFactory.accept(ResponseFactory.java:120)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:101)
 -> ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:42)
 -> ji.socketCommunication.Server.lambda$1(Server.java:192)
 -> java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
 -> java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
 -> java.base/java.lang.Thread.run(Thread.java:835)
		*/
		log((log)->{
			log.logTemplate(module, filename);
		});
		/*/
		if (!use) {
			return;
		}
		log("TEMPLATE: " + module + " " + filename);
		//*/
	}
	
	@Override
	public void log(Map<HttpServerProfilerEvent, Long> events) {
		//*
		log((log)->{
			for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
				log.addServerEvent(events.get(event), event);
			}
		});
		/*/
		if (!use) {
			return;
		}
		log("SERVER EVENTS: " + events);
		//*/
	}
	
	
	private void log(String text) {
		try {
			String filename = "profiler/" + Thread.currentThread().getId() + ".log";
			// new File(filename).createNewFile();
			Text.get().write((br)->{
				br.write(text);
				br.newLine();
			}, filename, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
