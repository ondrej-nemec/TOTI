package toti;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import ji.common.exceptions.NotImplementedYet;
import ji.common.structures.MapInit;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RequestParameters;
import ji.socketCommunication.http.server.RestApiResponse;
import ji.socketCommunication.http.server.WebSocket;
import ji.translator.Translator;
import toti.profiler.Profiler;
import toti.response.Response;
import toti.security.Identity;
import toti.templating.TemplateFactory;

public class ResponseFactoryToti {
	
	private final Profiler profiler;
//	private final DbViewerRouter dbViewer;
	private final List<String> developIps;
	private final TemplateFactory templateFactory;
	private final Translator translator;
	private final String charset;
	
	public ResponseFactoryToti(
			Profiler profiler, List<String> developIps,
			Translator translator, TemplateFactory templateFactory,
			String charset) {
		this.profiler = profiler;
		this.developIps = developIps;
		this.templateFactory = templateFactory;
		this.translator = translator;
		this.charset = charset;
	}

	public RestApiResponse getTotiResponse(
			HttpMethod method, String url, RequestParameters params,
			Identity identity, ResponseHeaders headers, Optional<WebSocket> websocket) {
		return getResponse(method, url, params, identity, headers)
			.getResponse(
				headers, templateFactory, translator.withLocale(identity.getLocale()),
				null /*authorizator*/, identity, null /* mapped url */, charset
			);
	}
	
	private Response getResponse(HttpMethod method, String url, RequestParameters params, Identity identity, ResponseHeaders headers) {
		if ((url.length() < 2 || url.equals("/index.html")) && developIps.contains(identity.getIP())) {// "/toti"->"" OR "/toti/"->"/"
			return getWelcomePage();
		}
		if (url.startsWith("db")) {
			return getDbViewer(method, url, params, identity, headers);
		}
		if (url.startsWith("/profiler")) {
			return getProfiler(method, params, identity);
		}
		return getTotiFiles(url);
	}
	
	private Response getProfiler(HttpMethod method, RequestParameters params, Identity identity) {
		if (profiler.isUse() && developIps.contains(identity.getIP())) {
			return profiler.getResponse(method, params);
		}
		return Response.getText(StatusCode.FORBIDDEN, "");
	}
	
	private Response getTotiFiles(String url) {
		return Response.getTemplate("/assets" + url, new MapInit<String, Object>().append("useProfiler", profiler.isUse()).toMap());
	}
	
	private Response getDbViewer(HttpMethod method, String url, RequestParameters params, Identity identity, ResponseHeaders headers) {
		throw new NotImplementedYet();
		// TODO return dbViewer.getResponse(method, url.substring(8), params, identity, headers);
	}
	
	private Response getWelcomePage() {
		return Response.getTemplate("index.html", new HashMap<>());
	}

}
