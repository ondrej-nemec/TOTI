package toti.dbviewer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import database.Database;
import database.DatabaseConfig;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.RequestParameters;
import toti.ResponseHeaders;
import toti.logging.TotiLogger;
import toti.response.Response;
import toti.security.Identity;

public class DbViewerRouter {

	private final static String COOKIE_NAME = "DB_Viewer";
	
	private final Map<String, DbViewer> viewers = new HashMap<>();
	
	public Response getResponse(
			HttpMethod method,
			String path, 
			RequestParameters params, 
			Identity identity, 
			ResponseHeaders responseHeaders) {
		String authCookie = identity.getCookieValue(COOKIE_NAME);
		if (authCookie == null) {
			responseHeaders.addHeader(
				"Set-Cookie: "
				+ COOKIE_NAME + "=" + RandomStringUtils.randomAlphanumeric(50)
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 900
			);
			return Response.getTemplate("/dbviewer/login.jsp", new HashMap<>());
		}
		if (params.toMap().containsKey("logout")) {
			viewers.remove(authCookie);
			responseHeaders.addHeader(
				"Set-Cookie: "
				+ COOKIE_NAME + "=" + authCookie
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 0
			);
			return Response.getTemplate("/dbviewer/login.jsp", new HashMap<>());
		}
		DbViewer viewer = viewers.get(authCookie);
		if (viewer == null && params.size() == 0) {
			return Response.getTemplate("/dbviewer/login.jsp", new HashMap<>());
		} else if (viewer == null) { // TODO method == HttpMethod.POST
			viewer = new DbViewer(new Database(createConfig(params), TotiLogger.getLogger("dbViewer")));
			viewers.put(authCookie, viewer);
		}
		return viewer.getResponse(method, path, params);
	}
	
	private DatabaseConfig createConfig(RequestParameters params) {
		return new DatabaseConfig(
			params.getString("type"), 
			params.getString("server"),
			params.getBoolean("runExternal") == null ? false : params.getBoolean("runExternal"), 
			params.getString("database"), 
			params.getString("name"),
			params.getString("psw"), 
			new LinkedList<>(), 
			"Europe/London", 
			5
		);
	}
	
}
