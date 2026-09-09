package toti.core.answers.session;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import toti.core.answers.Headers;
import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.tcpip.structures.RequestParameters;

public class IdentityFactory {
	
	private final Collection<Extension> extensions;
	private final SessionManager session;
	
	public IdentityFactory(Collection<Extension> extensions, SessionManager session) {
		this.extensions = extensions;
		this.session = session;
	}

	public Identity createIdentity(Headers requestHeaders, MapDictionary<String> queryParameters, RequestParameters bodyParameters, String ip) {
		Optional<CurrentSession> currentSession = session.restoreSession(requestHeaders, queryParameters, bodyParameters);

		Identity identity = new Identity(
			ip,
			currentSession.isPresent() ? currentSession.get().sessionSpace() : new HashMap<>(),
			Optional.ofNullable(currentSession.isPresent() ? currentSession.get().sessionId() : null),
			Optional.ofNullable(currentSession.isPresent() ? currentSession.get().csrfToken() : null),
			currentSession.isPresent() ? currentSession.get().user() : Optional.empty(),
			currentSession.isPresent() ? currentSession.get().isTokenVerified() : false
		);
		extensions.forEach((extension)->{
			extension.onRequestStart(
				identity, identity.getSessionSpace(extension),
				requestHeaders, queryParameters, bodyParameters
			);
		});
		return identity;
	}

	public void finalizeIdentity(Identity identity, Headers responseHeaders) throws IOException {
		extensions.forEach((extension)->{
			extension.onRequestEnd(
				identity, identity.getSessionSpace(extension), responseHeaders
			);
		});
		session.saveSession(responseHeaders, identity.getSessionId(), identity.getSessionSpaces(), identity.getUserMode(), identity.getUser());
	}

}
