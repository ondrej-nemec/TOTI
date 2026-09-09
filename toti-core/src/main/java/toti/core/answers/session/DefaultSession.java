package toti.core.answers.session;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import toti.core.answers.Headers;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.NamedThredFactory;
import toti.lib.tcpip.structures.RequestParameters;

public class DefaultSession implements SessionManager {

	class Session {
		String sessionId;
		String csrfToken;
		Long expiration;
		Map<String, MapDictionary<String>> sessionSpaces;
		Optional<Object> user;

		public Session(String sessionId, String csrfToken, Long expiration, Map<String, MapDictionary<String>> sessionSpaces, Optional<Object> user) {
			this.sessionId = sessionId;
			this.csrfToken = csrfToken;
			this.expiration = expiration;
			this.sessionSpaces = sessionSpaces;
			this.user = user;
		}

		CurrentSession create(String currentToken) {
			return new CurrentSession(sessionId, sessionSpaces, user, csrfToken, csrfToken != null && csrfToken.equals(currentToken));
		}
	}

	public final static String SESSION_COOKIE_NAME = "SessionID";
	public final static String SESSION_HEADER_NAME = "Authorization";
	public final static String CSRF_TOKEN_NAME = "_csrf_token";

	private final Map<String, Session> spaces = new ConcurrentHashMap<>();

	private final String basePath;
	private final Long maxAgeInSec;

	private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor(new NamedThredFactory("DefaultSession"));
	private Future<?> future;

	public DefaultSession(String basePath, Long maxAgeInSec) {
		this.basePath = basePath == null || basePath.equals("") ? "/" : basePath;
		this.maxAgeInSec = maxAgeInSec;
	}

	@Override
	public void onApplicationStart() throws Exception {
		if (maxAgeInSec == null) {
			return;
		}
		future = pool.scheduleWithFixedDelay(()->{
			spaces.keySet().forEach(sessionId->{
				if (check(sessionId) == null) {
					spaces.remove(sessionId);
				}
			});
		}, 1, 2, TimeUnit.MINUTES);
	}

	private Session check(String sessionId) {
		var session = spaces.get(sessionId);
		if (session == null) {
			return null;
		}
		if (session.expiration == null) {
			return session;
		}
		if (session.expiration >= ZonedDateTime.now().toEpochSecond()) {
			return session;
		}
		return null;
	}

	@Override
	public void onApplicationStop() throws Exception {
		if (future != null) {
			future.cancel(true);
		}
		pool.shutdown();
	}

	@Override
	public Optional<CurrentSession> restoreSession(
		Headers requestHeaders,
		MapDictionary<String> queryParams,
		RequestParameters requestBody
	) {
		String csrfToken = null;
		if (requestBody.containsKey(CSRF_TOKEN_NAME)) {
			csrfToken = requestBody.getString(CSRF_TOKEN_NAME);
			requestBody.remove(CSRF_TOKEN_NAME);
		}

		String sessionId = getHeaderToken(requestHeaders)
		.orElse(
			getCookieToken(requestHeaders)
			.orElse(generateSecret())
		);
		Session session = check(sessionId);
		if (session == null) {
			session = new Session(sessionId, generateSecret(), null, new HashMap<>(), Optional.empty());
			spaces.put(sessionId, session);
		}
		return Optional.of(session.create(csrfToken));
	}

	@Override
	public void saveSession(Headers responseHeaders, Optional<String> sessionId, Map<String, MapDictionary<String>> sessionSpace, UserMode userMode, Optional<Object> user) {
		if (userMode == UserMode.ANONYMOUS) {
			responseHeaders.addHeader(
				"Set-Cookie", 
				SESSION_COOKIE_NAME + "="
				+ "; HttpOnly"
				+ (basePath == null ? "" : "; Path=" + basePath)
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 0
			);
			spaces.remove(sessionId.get());
		} else {
			Long expiration = maxAgeInSec;
			if (expiration != null) {
				expiration += ZonedDateTime.now().toEpochSecond();
			}
			Session session = spaces.get(sessionId.get());
			// sessionspace is reference, no update needed
			session.expiration = expiration;
			session.user = user;

			responseHeaders.addHeader(
				"Set-Cookie",
				SESSION_COOKIE_NAME + "=" + sessionId.get()
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

	private String generateSecret() {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
			byte[] secret = new byte[32];
			sr.nextBytes(secret);
			return Base64.getEncoder().encodeToString(secret);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
	}

}
