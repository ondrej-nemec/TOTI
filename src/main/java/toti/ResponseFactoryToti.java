package toti;

import java.util.HashMap;
import java.util.List;

import ji.common.structures.MapInit;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RequestParameters;
import toti.profiler.Profiler;
import toti.response.Response;
import toti.security.Identity;

public class ResponseFactoryToti {
	
	private final Profiler profiler;
//	private final DbViewerRouter dbViewer;
	private final List<String> developIps;
	
	public ResponseFactoryToti(Profiler profiler, List<String> developIps) {
		this.profiler = profiler;
		this.developIps = developIps;
	}

	public Response getTotiResponse(HttpMethod method, String url, RequestParameters params, Identity identity, ResponseHeaders headers) {
		if (url.length() < 2 && developIps.contains(identity.getIP())) {// "/toti"->"" OR "/toti/"->"/"
			return getWelcomePage();
		}
		/*
		if (url.substring(6).startsWith("db")) {
			return dbViewer.getResponse(method, url.substring(8), params, identity, headers);
		}
		*/
		if (url.startsWith("/profiler")) {
			if (profiler.isUse() && developIps.contains(identity.getIP())) {
				return profiler.getResponse(method, params);
			}
			return Response.getText(StatusCode.FORBIDDEN, "");
		}
		return Response.getTemplate("/assets" + url.substring(5), new MapInit<String, Object>().append("useProfiler", profiler.isUse()).toMap());
	}
	
	private Response getWelcomePage() {
		return Response.getTemplate("index.html", new HashMap<>());
	}

}
