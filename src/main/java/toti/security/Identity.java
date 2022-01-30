package toti.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ji.translator.Locale;

public class Identity {

	private final String IP;
	private final Locale locale;
	private final Properties requestHeaders;
	private String token; // full session string
	private final boolean isApiAllowed; // resolved with token

	private String pageId;
	
	private String id; // id from token
	private long expired; // range, how long will be active
	private User user; // user loaded from cache
	
	private final List<String> responseHeaders = new LinkedList<>();
	
	protected Identity(String IP, Locale locale, Properties requestHeaders, String token, boolean isApiAllowed) {
		this.IP = IP;
		this.locale = locale;
		this.requestHeaders = requestHeaders;
		this.isApiAllowed = isApiAllowed;
		this.token = token;
	}
	
	protected void setUser(String id, long expired, User user) {
		this.id = id;
		this.expired = expired;
		this.user = user;
	}
	
	protected void loginUser(String token, String id, long expired, User user) {
		this.token = token;
		this.id = id;
		this.expired = expired;
		this.user = user;
	}
	
	protected long getExpirationTime() {
		return expired;
	}
	
	/**
	 * Token from auth header/cookie
	 * @return full token with hash, random,...
	 */
	protected String getToken() {
		return token;
	}
	
	protected void clear() {
		this.token = null;
		this.id = null;
		this.expired = -1;
		this.user = null;
	}
	
	protected String getId() {
		return id;
	}
	
	protected List<String> getResponseHeaders() {
		return responseHeaders;
	}
	
	/*************/
	
	public boolean isAnonymous() {
		return id == null;
	}
	
	public boolean isPresent() {
		return !isAnonymous();
	}
	
	public User getUser() {
		return user;
	}
	
	public <U extends User> U getUser(Class<U> clazz) {
		return clazz.cast(user);
	}
	
	public Properties getHeaders() {
		return requestHeaders;
	}

	public String getCookieValue(String cookieName) {
		return getCookieValue(requestHeaders, cookieName);
	}
	
	public String getIP() {
		return IP;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public boolean isApiAllowed() {
		return isApiAllowed;
	}
	
	public void addResponseHeader(String header) {
		responseHeaders.add(header);
	}
	
	// just probably
	public boolean isAsyncRequest() {
		String destination = requestHeaders.getProperty("Sec-Fetch-Dest");
		return destination != null && destination.equals("empty");
	}

	@Override
	public String toString() {
		return "Identity [IP=" + IP + ", locale=" + locale + ", token=" + token
				+ ", isApiAllowed=" + isApiAllowed + ", id=" + id + ", expired=" + expired //+ ", content=" + content
				+ ", user=" + user + ", requestHeaders=" + requestHeaders + "]";
	}

	// TODO test this method
	public static String getCookieValue(Properties requestHeaders, String cookieName) {
		if (requestHeaders.get("Cookie") != null) {
			String[] cookiesArray = requestHeaders.get("Cookie").toString().split(";");
			for (String cookies : cookiesArray) {
				String[] cookie = cookies.split("=", 2);
				if (cookie.length == 2 && cookie[0].trim().equals(cookieName)) {
					String value = cookie[1].trim();
					if (value.equals("null")) {
						return null;
					}
					return value;
				}
			}
		}
		return null;
	}
		
	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		if (this.pageId == null) {
			this.pageId = pageId;
		}
	}
	
}
