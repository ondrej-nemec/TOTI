package toti.core.answers.session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;

public class Identity {

	private final String IP;
	private final MapDictionary<String> scope;
	private final Map<String, MapDictionary<String>> sessionSpaces;

	private final Optional<String> sessionId;
	private final Optional<String> csrfToken;
	private Optional<Object> user;
	private final boolean isCsrfTokenVerified;
	private UserMode mode;

	protected Identity(
		String IP, Map<String, MapDictionary<String>> sessionSpaces, Optional<String> sessionId,
		Optional<String> csrfToken, Optional<Object> user, boolean isCsrfTokenVerified
	) {
		this.IP = IP;
		this.scope = MapDictionary.hashMap();
		this.sessionSpaces = sessionSpaces;
		this.sessionId = sessionId;
		this.csrfToken = csrfToken;
		this.user = user;
		this.isCsrfTokenVerified = isCsrfTokenVerified;
		this.mode = sessionId.isEmpty() ? UserMode.ANONYMOUS : user.isEmpty() ? UserMode.NOT_LOGGED_USER : UserMode.LOGGED_USER;
	}
	
	public void login(Object user) {
		this.user = Optional.of(user);
		this.mode = UserMode.LOGGED_USER;
	}
	
	public void logout() {
		this.user = Optional.empty();
		this.mode = UserMode.ANONYMOUS;
	}

	public String getCsrfToken() {
		return csrfToken.orElse("");
	}
	
	public boolean isAnonymous() {
		return mode == UserMode.ANONYMOUS;
	}
	
	public boolean isUserPresent() {
		return user.isPresent();
	}

	public boolean isCsrfTokenVerified() {
		return isCsrfTokenVerified;
	}

	/*************/
	
	protected MapDictionary<String> getSessionSpace(Extension extension) {
		return getSessionSpace(extension.getIdentifier());
	}

	public MapDictionary<String> getSessionSpace() {
		return getSessionSpace("");
	}

	public void addFlashMessage(String severity, String message) {
		getFlashes().add(new FlashMessage(severity, message));
	}

	public List<FlashMessage> getFlashMessages() {
		List<FlashMessage> flashes = getFlashes();
		List<FlashMessage> result = new ArrayList<>(flashes);
		flashes.clear();
		return result;
	}

	private List<FlashMessage> getFlashes() {
		MapDictionary<String> flashes = getSessionSpace("toti_flash");
		if (!flashes.containsKey("flash")) {
			flashes.put("flash", new LinkedList<>());
		}
		return flashes.getList("flash");
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

	/*******************/
	
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

	protected UserMode getUserMode() {
		return mode;
	}
	
	/**
	 * Returns user IP
	 * @return
	 */
	public String getIP() {
		return IP;
	}

	protected Optional<String> getSessionId() {
		return sessionId;
	}

	protected Map<String, MapDictionary<String>> getSessionSpaces() {
		return sessionSpaces;
	}

}
