package toti.security;

import java.util.Locale;
import java.util.Properties;

public class Identity {

	private final String IP;
	private final Locale locale;
	private final Properties requestHeaders;
	private String token;
	private final boolean isApiAllowed; // resolved with token

	private String id;
	private long expired;
	private String content; // content saved in token
	// TODO user // user loaded from cache

	
	protected Identity(String IP, Locale locale, Properties requestHeaders, String token, boolean isApiAllowed) {
		this.IP = IP;
		this.locale = locale;
		this.requestHeaders = requestHeaders;
		this.isApiAllowed = isApiAllowed;
		this.token = token;
	}
	
	protected void setUser(String id, long expired, String content) {
		this.id = id;
		this.expired = expired;
		this.content = content;
	}
	
	protected void loginUser(String token, String id, long expired, String content) {
		this.token = token;
		this.id = id;
		this.expired = expired;
		this.content = content;
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
		this.expired = 0;
		this.content = null;
	}
	
	/*************/
	
	public boolean isAnonymous() {
		return id == null;
	}
	
	public Properties getHeaders() {
		return requestHeaders;
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
}
