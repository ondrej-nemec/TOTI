package toti.dbviewer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.Headers;
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
			Headers responseHeaders) {
		String authCookie = identity.getCookieValue(COOKIE_NAME);
		/*
		if ("".equals(path)) {
			
		}
		System.err.println(params);
		System.err.println("path '" + path + "'"); // pouzit
		System.err.println(method);
		System.err.println(authCookie);
		*/
		switch (method) {
			case POST:
				if (authCookie == null && params.size() > 0) { // login
					authCookie = RandomStringUtils.randomAlphanumeric(50);
					responseHeaders.addHeader(
						"Set-Cookie",
						COOKIE_NAME + "=" + authCookie
						+ "; HttpOnly"
						+ "; Path=/toti/db"
						+ "; SameSite=Strict"
						+ "; Max-Age=" + 900
					);
					viewers.put(authCookie, new DbViewer(new Database(createConfig(params), TotiLogger.getLogger("dbViewer"))));
					return Response.getRedirect("/toti/db");
				} else if (params.containsKey("logout")) {
					// logout
					return logOut(authCookie, responseHeaders);
				}
			case PATCH:
			case GET:
			case DELETE:
			case PUT:
			default:
				if (authCookie == null) { // get login page
					return Response.getTemplate("/dbviewer/login.jsp", new HashMap<>());
				}
				DbViewer viewer = viewers.get(authCookie);
				if (viewer == null) { // get login page
					return logOut(authCookie, responseHeaders);
				}
				return viewer.getResponse(method, path, params);
		}
		/*
		if (authCookie == null) {
			responseHeaders.addHeader(
				"Set-Cookie: "
				+ COOKIE_NAME + "=" + RandomStringUtils.randomAlphanumeric(50)
				+ "; HttpOnly"
				+ "; Path=/toti/db"
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
				+ "; Path=/toti/db"
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
		*/
	}
	
	private Response logOut(String authCookie, Headers responseHeaders) {
		if (authCookie != null) {
			viewers.remove(authCookie);
			responseHeaders.addHeader(
				"Set-Cookie",
				COOKIE_NAME + "=" + authCookie
				+ "; HttpOnly"
				+ "; Path=/toti/db"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 0
			);
		}
		return Response.getRedirect("/toti/db");
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
			5
		);
	}
	
}
