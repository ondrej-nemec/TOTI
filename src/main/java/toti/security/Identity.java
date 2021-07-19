package toti.security;

import java.util.Properties;

import translator.Locale;

public class Identity {

	private final String IP;
	private final Locale locale;
	private final Properties requestHeaders;
	private String token;
	private final boolean isApiAllowed; // resolved with token

	private String id;
	private long expired;
	private String content; // content saved in token
	private User user; // user loaded from cache
	
	protected Identity(String IP, Locale locale, Properties requestHeaders, String token, boolean isApiAllowed) {
		this.IP = IP;
		this.locale = locale;
		this.requestHeaders = requestHeaders;
		this.isApiAllowed = isApiAllowed;
		this.token = token;
	}
	
	protected void setUser(String id, long expired, String content, User user) {
		this.id = id;
		this.expired = expired;
		this.content = content;
		this.user = user;
	}
	
	protected void loginUser(String token, String id, long expired, String content, User user) {
		this.token = token;
		this.id = id;
		this.expired = expired;
		this.content = content;
		this.user = user;
	}
	
	protected long getExpirationTime() {
		return expired;
	}
	
	protected String getToken() {
		return token;
	}
	
	protected void clear() {
		this.token = null;
		this.id = null;
		this.expired = -1;
		this.content = null;
		this.user = null;
	}
	
	protected String getId() {
		return id;
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
	
	public String getContent() {
		return content;
	}
	
	public boolean isApiAllowed() {
		return isApiAllowed;
	}

	@Override
	public String toString() {
		return "Identity [IP=" + IP + ", locale=" + locale + ", token=" + token
				+ ", isApiAllowed=" + isApiAllowed + ", id=" + id + ", expired=" + expired + ", content=" + content
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
	
}
