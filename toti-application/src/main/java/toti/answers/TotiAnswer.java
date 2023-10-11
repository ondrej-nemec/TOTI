package toti.answers;

import java.util.List;

import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.Headers;
import toti.ServerException;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.security.Identity;
import toti.templating.TemplateFactory;

public class TotiAnswer {
	
	private final List<String> developIps;
	private final TemplateFactory templateFactory;
	private final Translator translator;
	
	public TotiAnswer(List<String> developIps, TemplateFactory templateFactory, Translator translator) {
		this.developIps = developIps;
		this.templateFactory = templateFactory;
		this.translator = translator;
	}
	
	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request, Headers requestHeaders,
			Identity identity, Headers responseHeaders, String charset
		) throws ServerException {
		return getResponse(request.getPlainUri(), Request.fromRequest(request, requestHeaders), identity, responseHeaders)
				.getResponse(
					request.getProtocol(), responseHeaders, identity,
					new ResponseContainer(
						translator.withLocale(identity.getLocale()), null, null, templateFactory, null
					), 
					charset
				);
	}
	
	protected Response getResponse(String url, Request request, Identity identity, Headers responseHeaders) {
		boolean isDevelopReqeust = developIps.contains(identity.getIP());
		url = url.substring(5);
		switch (url.toLowerCase()) {
			case "":
			case "/":
			case "/index":
			case "/index.html":
				if (isDevelopReqeust) {
					return getWelcomePage();
				}
				break;
			case ".js":
			case "/toti.js":
				// TODO script reponse
		}
		
		/*if (url.startsWith("db")) {
			return getDbViewer(method, url, params, identity, responseHeaders);
		}
		if (url.startsWith("/profiler")) {
			return getProfiler(method, params, identity);
		}*/
		// TODOreturn getTotiFiles(url, identity);
		return Response.create(StatusCode.NOT_FOUND).getEmpty();
	}
	
	private Response getWelcomePage() {
		return Response.create(StatusCode.OK).getFile("toti/assets/index.html");
	}
	
	/*
		private final Profiler profiler;
	private final Generate generator = new Generate();
//	private final DbViewerRouter dbViewer;
	
	
	
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

	*/
}
