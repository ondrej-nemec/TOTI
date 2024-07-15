package toti.extensions.auth;

import java.util.Map;
import java.util.Optional;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import toti.answers.Headers;
import toti.answers.request.AuthMode;
import toti.answers.request.Identity;
import toti.application.register.Register;
import toti.extensions.Extension;
import toti.http.RequestParameters;

public class AuthenticationExtension implements Extension, toti.extensions.AuthenticationExtension {

	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";
	public static final String CSRF_TOKEN_PARAMETER = "_csrf_token";
	
	private SessionUserProvider sessionUserProvider = null;

	public void setSessionUserProvider(SessionUserProvider sessionUserProvider) {
		this.sessionUserProvider = sessionUserProvider;
	}
	
	@Override
	public String getIdentifier() {
		return "toti-auth";
	}

	@Override
	public void init(Env appEnv, Register register) {}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {
		if (sessionUserProvider == null) {
			throw new RuntimeException("AuthenticationExtension is registered, but SessionUserProvider is missing.");
		}
		Optional<String> cookieToken = getCookieToken(requestHeaders);
		Optional<String> csrfToken = getCsrfToken(requestBody);
		Optional<String> headerToken = getHeaderToken(requestHeaders);
		
		Optional<LoggedUser> user = sessionUserProvider.getUser(headerToken, cookieToken, csrfToken);
		if (user.isPresent()) {
			identity.login(user.get(), selectLoginMode(headerToken, csrfToken, cookieToken));
		}
	}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {
		if (identity.isAnonymous() || identity.getUser(LoggedUser.class).getExpirationTime()  < 0) {
			responseHeaders.addHeader(
				"Set-Cookie", 
				SESSION_COOKIE_NAME + "="
					+ "; HttpOnly"
					+ "; Path=/"
					+ "; SameSite=Strict"
					+ "; Max-Age=" + 0
			);
		} else if (identity.getUser(LoggedUser.class).getCookieToken().isPresent())  {
			responseHeaders.addHeader(
				"Set-Cookie",
				SESSION_COOKIE_NAME + "=" + identity.getUser(LoggedUser.class).getCookieToken().get()
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + (identity.getUser(LoggedUser.class).getExpirationTime() / 1000)
			);
		}
		Optional<LoggedUser> user = Optional.empty();
		if (identity.isPresent()) {
			user = Optional.of(identity.getUser(LoggedUser.class));
		}
		sessionUserProvider.saveUser(user);
	}

	@Override
	public void onApplicationStart() throws Exception {}

	@Override
	public void onApplicationStop() throws Exception {}

	private AuthMode selectLoginMode(Optional<String> headerToken, Optional<String> csrfToken,
		Optional<String> cookieToken) {
		if (headerToken.isPresent()) {
			return AuthMode.HEADER;
		} else if (csrfToken.isPresent() && cookieToken.isPresent()) {
			return AuthMode.COOKIE_AND_CSRF;
		} else if (cookieToken.isPresent()) {
			return AuthMode.COOKIE;
		}
		return AuthMode.NO_TOKEN;
	}

	private Optional<String> getHeaderToken(Headers requestHeaders) {
		if (requestHeaders.containsHeader(SESSION_HEADER_NAME)) {
			String[] vals = requestHeaders.getHeader(SESSION_HEADER_NAME).toString().split(" ", 2);
			if (vals.length == 2) {
				return Optional.of(vals[1]);
			}
		}
		return Optional.empty();
	}

	private Optional<String> getCookieToken(Headers requestHeaders) {
		return requestHeaders.getCookieValue(SESSION_COOKIE_NAME);
	}
	
	public Optional<String> getCsrfToken(RequestParameters parameters) {
		if (parameters.containsKey(CSRF_TOKEN_PARAMETER)) {
			String csrfToken = parameters.getString(CSRF_TOKEN_PARAMETER);
			parameters.remove(CSRF_TOKEN_PARAMETER);
			return Optional.of(csrfToken);
		}
		return Optional.empty();
	}

	@Override
	public boolean isAllowed(Object user, Map<String, Object> params) {
		return sessionUserProvider.isAllowed(LoggedUser.class.cast(user), params);
	}

	@Override
	public String getNotLoggedUserRedirect(String backlink) {
		return sessionUserProvider.getNotLoggedUserRedirect(backlink);
	}

}
