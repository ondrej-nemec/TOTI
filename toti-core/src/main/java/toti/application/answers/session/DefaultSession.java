package toti.application.answers.session;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
application
import org.apache.commons.lang3.RandomStringUtils;

import toti.application.answers.Headers;
import toti.lib.common.structures.MapDictionary;
import toti.lib.tcpip.structures.RequestParameters;

public class DefaultSession implements SessionManager {

	// TODO
	/*
	overeni pristupu - @Secured? nebo jinak? aby to bylo jednoduche dat vsude => jako v edovi Sessionmanager?
	jak redirect s backlink při neprihlasenem uzivateli? => vyuzit request?
	*/

	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";

	private final Map<String, CurrentSession> spaces = new HashMap<>();
	private String salt;

	private final String basePath;
	private final Long maxAgeInSec;

	public DefaultSession(String basePath, Long maxAgeInSec) {
		this.basePath = basePath == null || basePath.equals("") ? "/" : basePath;
		this.maxAgeInSec = maxAgeInSec;
	}

	@Override
	public CurrentSession restoreSession(
		Headers requestHeaders,
		MapDictionary<String> queryParams,
		RequestParameters requestBody
	) {
		String token = getHeaderToken(requestHeaders)
		.orElse(
			getCookieToken(requestHeaders)
			.orElse(RandomStringUtils.randomAlphabetic(50))
		);
		if (spaces.containsKey(token)) {
			return spaces.get(token);
		}
		return new CurrentSession(token, new HashMap<>(), Optional.empty());
	}

	@Override
	public void saveSession(Headers responseHeaders, String sessionId, Map<String, MapDictionary<String>> sessionSpace, Optional<Object> user) {
		spaces.put(sessionId, new CurrentSession(sessionId, sessionSpace, user));
		if (user.isEmpty()) {
			responseHeaders.addHeader(
				"Set-Cookie", 
				SESSION_COOKIE_NAME + "="
				+ "; HttpOnly"
				+ (basePath == null ? "" : "; Path=" + basePath)
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 0
			);
		} else {
			responseHeaders.addHeader(
				"Set-Cookie",
				SESSION_COOKIE_NAME + "=" + sessionId
				+ "; HttpOnly"
				+ (basePath == null ? "" : "; Path=" + basePath)
				+ "; SameSite=Strict"
				+ (maxAgeInSec == null ? "" : "; Max-Age=" + maxAgeInSec)
			);
		}
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

	@Override
	public String getCsrfTokenSalt() {
		if (salt == null) {
			try {
				SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
				byte[] saltBytes = new byte[32];
				sr.nextBytes(saltBytes);
				salt = new String(saltBytes);
			} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
				throw new RuntimeException(e);
			}
		}
		return salt;
	}

}
