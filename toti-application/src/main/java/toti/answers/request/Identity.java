package toti.answers.request;

import ji.common.structures.MapDictionary;
import toti.extensions.Extension;

public class Identity {

	private final String IP;
	
	// TODO IMPROVE will be Level? or something else more general? or in Steps?
	private AuthMode loginMode = AuthMode.NO_TOKEN;
		
	private Object user;
	
	private MapDictionary<String> sessionSpaces;
	
	protected Identity(String IP) {
		this.IP = IP;
		this.sessionSpaces = MapDictionary.hashMap();
	}
	
	public void login(Object user, AuthMode loginMode) {
		this.user = user;
		this.loginMode = loginMode;
	}
	
	public void logout() {
		clear();
	}
	
	protected void clear() {
		this.user = null;
		this.loginMode = AuthMode.NO_TOKEN;
	}
	
	/*************/
	
	public MapDictionary<String> getSessionSpace(Extension extension) {
		return getSessionSpace(extension.getIdentifier());
	}
	
	public MapDictionary<String> getSessionSpace(String name) {
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
	
	/**
	 * Returns user as defined class. This class must extends of User
	 * @param clazz
	 * @return
	 */
	public <U> U getUser(Class<U> clazz) {
		return clazz.cast(user);
	}
	
	/**
	 * Returns user IP
	 * @return
	 */
	public String getIP() {
		return IP;
	}
	
}
