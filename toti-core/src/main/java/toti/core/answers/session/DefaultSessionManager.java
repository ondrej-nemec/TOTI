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

public class DefaultSessionManager implements SessionManager {

	protected static class Session {
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
			result = prime * result + ((csrfToken == null) ? 0 : csrfToken.hashCode());
			result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
			result = prime * result + ((sessionSpaces == null) ? 0 : sessionSpaces.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Session other = (Session) obj;
			if (sessionId == null) {
				if (other.sessionId != null)
					return false;
			} else if (!sessionId.equals(other.sessionId))
				return false;
			if (csrfToken == null) {
				if (other.csrfToken != null)
					return false;
			} else if (!csrfToken.equals(other.csrfToken))
				return false;
			if (expiration == null) {
				if (other.expiration != null)
					return false;
			} else if (!expiration.equals(other.expiration))
				return false;
			if (sessionSpaces == null) {
				if (other.sessionSpaces != null)
					return false;
			} else if (!sessionSpaces.equals(other.sessionSpaces))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Session{");
			sb.append("sessionId=").append(sessionId);
			sb.append(", csrfToken=").append(csrfToken);
			sb.append(", expiration=").append(expiration);
			sb.append(", sessionSpaces=").append(sessionSpaces);
			sb.append(", user=").append(user);
			sb.append('}');
			return sb.toString();
		}
		
	}

	public final static String SESSION_COOKIE_NAME = "SessionID";
	public final static String SESSION_HEADER_NAME = "Authorization";
	public final static String CSRF_TOKEN_NAME = "_csrf_token";

	private final Map<String, Session> spaces;

	private final String basePath;
	private final Long maxAgeInSec;

	private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor(new NamedThredFactory("DefaultSession"));
	private Future<?> future;

	public DefaultSessionManager(String basePath, Long maxAgeInSec) {
		this(basePath, maxAgeInSec, new ConcurrentHashMap<>());
	}

	protected DefaultSessionManager(String basePath, Long maxAgeInSec, Map<String, Session> spaces) {
		this.basePath = basePath == null || basePath.equals("") ? "/" : basePath;
		this.maxAgeInSec = maxAgeInSec;
		this.spaces = spaces;
	}

	@Override
	public void onApplicationStart() throws Exception {
		if (maxAgeInSec == null) {
			return;
		}
		future = pool.scheduleWithFixedDelay(()->{
			runCheck(ZonedDateTime.now());
		}, 1, 2, TimeUnit.MINUTES);
	}

	protected void runCheck(ZonedDateTime now) {
		for(var iterator = spaces.entrySet().iterator(); iterator.hasNext(); ) {
			var entry = iterator.next();
			if(check(entry.getKey(), now) == null) {
				iterator.remove();
			}
		}
	}

	protected Session check(String sessionId, ZonedDateTime now) {
		var session = spaces.get(sessionId);
		if (session == null) {
			return null;
		}
		if (session.expiration == null) {
			return session;
		}
		if (session.expiration >= now.toEpochSecond()) {
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
		return restoreSession(requestHeaders, queryParams, requestBody, ZonedDateTime.now());
	}

	protected Optional<CurrentSession> restoreSession(
		Headers requestHeaders,
		MapDictionary<String> queryParams,
		RequestParameters requestBody,
		ZonedDateTime now
	) {
		String csrfToken = null;
		if (requestBody.containsKey(CSRF_TOKEN_NAME)) {
			csrfToken = requestBody.getString(CSRF_TOKEN_NAME);
			requestBody.remove(CSRF_TOKEN_NAME);
		}
		Session session = null;
		Optional<String> token;
		if ((token = getHeaderToken(requestHeaders)).isPresent()) {
			session = check(token.get(), now);
		} else if ((token = getCookieToken(requestHeaders)).isPresent()) {
			session = check(token.get(), now);
		}
		if (session == null) {
			String sessionId = generateSecret();
			session = new Session(
				sessionId, generateSecret(), maxAgeInSec == null ? null : now.toEpochSecond(),
				new HashMap<>(), Optional.empty()
			);
			spaces.put(sessionId, session);
		}
		return Optional.of(session.create(csrfToken));
	}

	@Override
	public void saveSession(Headers responseHeaders, Optional<String> sessionId, Map<String, MapDictionary<String>> sessionSpace, UserMode userMode, Optional<Object> user) {
		saveSession(responseHeaders, sessionId, sessionSpace, userMode, user, ZonedDateTime.now());
	}

	protected void saveSession(
		Headers responseHeaders, Optional<String> sessionId, Map<String, MapDictionary<String>> sessionSpace,
		UserMode userMode, Optional<Object> user, ZonedDateTime now
	) {
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
				expiration += now.toEpochSecond();
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

	protected String generateSecret() {
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
