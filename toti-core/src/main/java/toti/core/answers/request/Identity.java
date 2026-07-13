package toti.core.answers.request;

import java.util.Map;
import java.util.Optional;

import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;

public class Identity {

	private final String IP;
		
	private Optional<Object> user;

	private final Map<String, MapDictionary<String>> sessionSpaces;
	private final String sessionId;
	private final CsrfToken csrfToken;

	private final MapDictionary<String> scope;
	
	protected Identity(String IP, String sessionId, Map<String, MapDictionary<String>> sessionSpaces, Optional<Object> user, CsrfToken csrfToken) {
		this.IP = IP;
		this.sessionSpaces = sessionSpaces;
		this.sessionId = sessionId;
		this.user = user;
		this.csrfToken = csrfToken;
		this.scope = MapDictionary.hashMap();
	}
	
	public void login(Object user) {
		this.user = Optional.of(user);
	}
	
	public void logout() {
		this.user = Optional.empty();
	}

	/*************/

	public CsrfToken getCsrfToken() {
		return csrfToken;
	}
	
	protected MapDictionary<String> getSessionSpace(Extension extension) {
		return getSessionSpace(extension.getIdentifier());
	}

	public MapDictionary<String> getSessionSpace() {
		return getSessionSpace("");
	}
	
	private MapDictionary<String> getSessionSpace(String name) {
		if (!sessionSpaces.containsKey(name)) {
			sessionSpaces.put(name, MapDictionary.hashMap());
		}
		return sessionSpaces.get(name);
	}

	public <T> T getScope(Class<T> clazz) {
		return scope.getDictionaryValue(clazz.getCanonicalName()).getValue(clazz);
	}

	public <T> void setScope(T t) {
		scope.put(t.getClass().getCanonicalName(), t);
	}

	public <T> void setScope(Class<?> clazz, T t) {
		scope.put(clazz.getCanonicalName(), t);
	}

	public <T> void removeScope(Class<T> clazz) {
		scope.remove(clazz.getCanonicalName());
	}
	
	public boolean isAnonymous() {
		return user.isEmpty();
	}
	
	public boolean isPresent() {
		return !isAnonymous();
	}
	
	/**
	 * Returns user as defined class. This class must extends of User
	 * @param clazz
	 * @return
	 */
	public <U> U getUser(Class<U> clazz) {
		return clazz.cast(user.orElse(null));
	}
	
	public Optional<Object> getUser() {
		return user;
	}
	
	/**
	 * Returns user IP
	 * @return
	 */
	public String getIP() {
		return IP;
	}

	protected String getSessionId() {
		return sessionId;
	}

	protected Map<String, MapDictionary<String>> getSessionSpaces() {
		return sessionSpaces;
	}

}
