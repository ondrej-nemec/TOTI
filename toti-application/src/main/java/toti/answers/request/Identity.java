package toti.answers.request;

import ji.common.structures.MapDictionary;
import ji.translator.Locale;
import toti.extensions.OnSession;

public class Identity {

	private final String IP;
	private final Locale locale;
	
	private AuthMode loginMode = AuthMode.NO_TOKEN;
		
	private LoggedUser user;
	
	private MapDictionary<String> sessionSpaces;
	
	protected Identity(String IP, Locale locale) {
		this.IP = IP;
		this.locale = locale;
		this.sessionSpaces = MapDictionary.hashMap();
	}
	
	public void login(LoggedUser user) {
		setUser(user);
	}
	
	public void logout() {
		clear();
	}
	
	protected void setUser(LoggedUser user) {
		this.user = user;
		if (user.getHeaderToken().isPresent()) {
			loginMode = AuthMode.HEADER;
		} else if (user.getCsrfToken().isPresent() && user.getCookieToken().isPresent()) {
			loginMode = AuthMode.COOKIE_AND_CSRF;
		} else if (user.getCookieToken().isPresent()) {
			loginMode = AuthMode.COOKIE;
		}
	}
	
	protected void clear() {
		this.user = null;
		this.loginMode = AuthMode.NO_TOKEN;
	}
	
	/*************/
	
	protected MapDictionary<String> getSessionSpace(OnSession session) {
		return getSessionSpace(session.getIdentifier());
	}
	
	protected MapDictionary<String> getSessionSpace(String name) {
		if (!sessionSpaces.containsKey(name)) {
			sessionSpaces.put(name, MapDictionary.hashMap());
		}
		return sessionSpaces.getDictionaryMap(name);
	}
	
	public AuthMode getLoginMode() {
		return loginMode;
	}
	
	public boolean isAnonymous() {
		return user == null;
	}
	
	public boolean isPresent() {
		return !isAnonymous();
	}
	
	public LoggedUser getUser() {
		return user;
	}
	
	/**
	 * Returns user as defined class. This class must extends of User
	 * @param clazz
	 * @return
	 */
	public <U extends LoggedUser> U getUser(Class<U> clazz) {
		return clazz.cast(user);
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
	
}
