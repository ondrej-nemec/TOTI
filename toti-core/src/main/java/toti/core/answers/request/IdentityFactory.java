package toti.core.answers.request;

import java.io.IOException;
import java.util.Collection;

import toti.core.answers.Headers;
import toti.core.answers.session.CurrentSession;
import toti.core.answers.session.SessionManager;
import toti.core.extensions.Extension;
import toti.lib.common.exceptions.HashException;
import toti.lib.common.functions.Hash;
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
		CurrentSession currentSession = session.restoreSession(requestHeaders, queryParameters, bodyParameters);
		Identity identity = new Identity(
			ip, currentSession.sessionId(), currentSession.sessionSpace(),
			currentSession.user(), createCsrfToken(currentSession.sessionId(), bodyParameters)
		);
		extensions.forEach((extension)->{
			extension.onRequestStart(
				identity, identity.getSessionSpace(extension),
				requestHeaders, queryParameters, bodyParameters
			);
		});
		return identity;
	}
	
	private CsrfToken createCsrfToken(String sessionId, RequestParameters parameters) {
		String csrfTokenName = "_csrf_token";
		String csrfToken = null;
		if (parameters.containsKey(csrfTokenName)) {
			csrfToken = parameters.getString(csrfTokenName);
			parameters.remove(csrfTokenName);
		}
		try {
			Hash hash = Hash.getSha256();
			return new CsrfToken(
				csrfTokenName,
				hash.toHash(sessionId, session.getCsrfTokenSalt()),
				csrfToken == null ? false : hash.compare(sessionId, csrfToken, session.getCsrfTokenSalt())
			);
		} catch (HashException e) {
			// parsed to runtime exception because of using predefined hash alghoritm
			throw new RuntimeException(e.getCause());
		}
	}

	public void finalizeIdentity(Identity identity, Headers responseHeaders) throws IOException {
		extensions.forEach((extension)->{
			extension.onRequestEnd(
				identity, identity.getSessionSpace(extension), responseHeaders
			);
		});
		session.saveSession(responseHeaders, identity.getSessionId(), identity.getSessionSpaces(), identity.getUser());
	}

}
