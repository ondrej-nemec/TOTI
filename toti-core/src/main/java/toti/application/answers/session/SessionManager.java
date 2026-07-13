package toti.application.answers.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import toti.application.answers.Headers;
import toti.lib.common.structures.MapDictionary;
import toti.lib.tcpip.structures.RequestParameters;

public interface SessionManager {

	CurrentSession restoreSession(Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody);

	void saveSession(Headers responseHeaders, String sessionId, Map<String, MapDictionary<String>> sessionSpace, Optional<Object> user);

	String getCsrfTokenSalt();

	static SessionManager empty() {
		return new SessionManager() {
			@Override public CurrentSession restoreSession(Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody) {
				return new CurrentSession("", new HashMap<>(), null);
			}
			@Override public void saveSession(
				Headers responseHeaders, String sessionId, Map<String, MapDictionary<String>> sessionSpace, Optional<Object> user
			) {}
			@Override public String getCsrfTokenSalt() { return ""; }
		};
	}

}
