package toti.security;

import java.util.LinkedList;
import java.util.List;

import ji.translator.Locale;
import toti.Headers;

public class Identity {

	private final String IP;
	private final Locale locale;
	private final Headers requestHeaders;
	private String token; // full session string
	
	private AuthMode loginMode = AuthMode.NO_TOKEN;

	private String pageId;
	
	private String id; // id from token
	private long expired; // range, how long will be active
	private User user; // user loaded from cache
	
	private String csrf; // for using in form, not from request
	
	private final List<String> responseHeaders = new LinkedList<>();
	
	protected Identity(String IP, Locale locale, Headers requestHeaders, String token, AuthMode loginMode) {
		this.IP = IP;
		this.locale = locale;
		this.requestHeaders = requestHeaders;
		this.loginMode = loginMode;
		this.token = token;
	}
	
	protected void setUser(String id, String csrf, long expired, User user, boolean isCsrf) {
		this.id = id;
		this.expired = expired;
		this.user = user;
		this.csrf = csrf;
		if (isCsrf && loginMode == AuthMode.COOKIE) {
			this.loginMode = AuthMode.COOKIE_AND_CSRF;
		}
	}
	
	protected void loginUser(String token, String csrf, String id, long expired, User user) {
		this.token = token;
		this.csrf = csrf;
		this.id = id;
		this.expired = expired;
		this.user = user;
	}
	
	public long getExpirationTime() {
		return expired;
	}
	
	public AuthMode getLoginMode() {
		return loginMode;
	}
	
	/**
	 * Token from auth header/cookie
	 * @return full token with hash, random,...
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * CSFR token related to this Identity (if user is loged)
	 * This value is not from request, it is created on login
	 * Is used for sending in form or for verifing
	 * @return CSRF token in String
	 */
	public String getCsrfToken() {
		return csrf;
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
	
	protected boolean hasUserChange() {
		return user != null && user.hasChange();
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
	
	/**
	 * Returns user as defined class. This class must extends of User
	 * @param clazz
	 * @return
	 */
	public <U extends User> U getUser(Class<U> clazz) {
		return clazz.cast(user);
	}
	
	/**
	 * Request headers
	 * @return
	 */
	public Headers getHeaders() {
		return requestHeaders;
	}

	/**
	 * Returns cookie value by name
	 * @param cookieName
	 * @return cookie value or null
	 */
	public String getCookieValue(String cookieName) {
		return getCookieValue(requestHeaders, cookieName);
	}
	
	/**
	 * Returns user IP
	 * @return
	 */
	public String getIP() {
		return IP;
	}
	
	/**
	 * Provide user preferenced language
	 * @return Locale
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Add header to response
	 * @param header
	 */
	public void addResponseHeader(String header) {
		responseHeaders.add(header);
	}
	
	/**
	 * Determite if request is brower link or not
	 * <strong>Resutl is not 100% sure.</strong>
	 * @return true if request is asychronious
	 */
	public boolean isAsyncRequest() {
		Object destination = requestHeaders.getHeader("Sec-Fetch-Dest");
		return destination != null && destination.toString().equals("empty");
	}

	@Override
	public String toString() {
		return "Identity [IP=" + IP + ", locale=" + locale + ", token=" + token
				+ ", loginMode=" + loginMode + ", id=" + id + ", expired=" + expired //+ ", content=" + content
				+ ", user=" + user + ", requestHeaders=" + requestHeaders + "]";
	}

	// TODO test this method
	public static String getCookieValue(Headers requestHeaders, String cookieName) {
		if (requestHeaders.containsHeader("Cookie")) {
			String[] cookiesArray = requestHeaders.getHeader("Cookie").toString().split(";");
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
