package toti.answers;

import java.util.LinkedList;
import java.util.List;

import toti.Headers;
import toti.ServerException;
import toti.security.Identity;

public class TotiAnswer {
	
	// TODO
	/*
	moznost nacitat JS soubory z ext - dasich modulu
	pridavat ext: profiler, db, ....
	*/
	
	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request,
			Identity identity, Headers responseHeaders, String charset
		) throws ServerException {
		return null;
	}
	/*
		private final Profiler profiler;
	private final Generate generator = new Generate();
//	private final DbViewerRouter dbViewer;
	
	private final List<String> developIps;
	private final TemplateFactory templateFactory;
	private final Translator translator;
	private final String charset;
	
	
	public ji.socketCommunication.http.structures.Response getTotiResponse(
			String url, Request request,
			Identity identity, Headers responseHeaders, Optional<WebSocket> websocket) {
		return getResponse(request.getMethod(), url, request.getBodyInParameters(), identity, responseHeaders)
			.getResponse(
				request.getProtocol(), responseHeaders, identity,
				new ResponseContainer(
					translator.withLocale(identity.getLocale()), null, null, templateFactory, null
				), 
				charset
			);
	}
	
	private Response getResponse(HttpMethod method, String url, RequestParameters params, Identity identity, Headers responseHeaders) {
		if ((url.length() < 2 || url.equals("/index.html")) && developIps.contains(identity.getIP())) {// "/toti"->"" OR "/toti/"->"/"
			return getWelcomePage();
		}
		if (url.startsWith("db")) {
			return getDbViewer(method, url, params, identity, responseHeaders);
		}
		if (url.startsWith("/profiler")) {
			return getProfiler(method, params, identity);
		}
		if (url.startsWith("/generate")) {
			return getGenerate(method, url.substring(9), params);
		}
		return getTotiFiles(url, identity);
	}
	
	private Response getProfiler(HttpMethod method, RequestParameters params, Identity identity) {
		if (profiler.isUse() && developIps.contains(identity.getIP())) {
			return profiler.getResponse(method, params);
		}
		return Response.getText(StatusCode.FORBIDDEN, "");
	}
	
	private Response getTotiFiles(String url, Identity identity) {
		return Response.getTemplate(
			"/assets" + url, 
			new MapInit<String, Object>()
			.append("useProfiler", profiler.isUse() && developIps.contains(identity.getIP()))
			.toMap()
		);
	}
	
	private Response getDbViewer(HttpMethod method, String url, RequestParameters params, Identity identity, Headers responseHeaders) {
		throw new NotImplementedYet();
		// TODO return dbViewer.getResponse(method, url.substring(8), params, identity, headers);
	}
	
	private Response getGenerate(HttpMethod method, String url, RequestParameters params) {
		if (url.equals("/do") && method == HttpMethod.POST) {
			return generator.generate(params);
		}
		return generator.getPage();
	}
	
	private Response getWelcomePage() {
		return Response.getTemplate("index.html", new HashMap<>());
	}

	*/
}
