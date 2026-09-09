package toti.core.answers.session;

import java.util.Map;
import java.util.Optional;

import toti.core.answers.Headers;
import toti.lib.common.structures.MapDictionary;
import toti.lib.tcpip.structures.RequestParameters;

public interface SessionManager {

	Optional<CurrentSession> restoreSession(Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody);

	void saveSession(Headers responseHeaders, Optional<String> sessionId, Map<String, MapDictionary<String>> sessionSpace, UserMode userMode, Optional<Object> user);

	void onApplicationStart() throws Exception;
	
	void onApplicationStop() throws Exception;

	static SessionManager empty() {
		return new SessionManager() {
			@Override public Optional<CurrentSession> restoreSession(Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody) {
				return Optional.empty();
			}
			@Override public void saveSession(
				Headers responseHeaders, Optional<String> sessionId, Map<String, MapDictionary<String>> sessionSpace, UserMode userMode, Optional<Object> user
			) {}
			@Override public void onApplicationStart() throws Exception {}
			@Override public void onApplicationStop() throws Exception {}
		};
	}

}
