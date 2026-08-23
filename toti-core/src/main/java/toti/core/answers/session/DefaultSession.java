package toti.core.answers.session;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import toti.core.answers.Headers;
import toti.core.answers.request.UserMode;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.NamedThredFactory;
import toti.lib.common.structures.Tuple2;
import toti.lib.tcpip.structures.RequestParameters;

public class DefaultSession implements SessionManager {

	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";

	private final Map<String, Tuple2<Long, CurrentSession>> spaces = new ConcurrentHashMap<>();
	private String salt;

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
			spaces.keySet().forEach(token->check(token));
		}, 1, 2, TimeUnit.MINUTES);
	}

	private CurrentSession check(String token) {
		var session = spaces.get(token);
		if (session == null) {
			return null;
		}
		if (session._1() == null) {
			return session._2();
		}
		if (session._1() >= ZonedDateTime.now().toEpochSecond()) {
			return session._2();
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
		CurrentSession session = check(token);
		if (session != null) {
			return session;
		}
		return new CurrentSession(token, new HashMap<>(), Optional.empty());
	}

	@Override
	public void saveSession(Headers responseHeaders, String sessionId, Map<String, MapDictionary<String>> sessionSpace, UserMode userMode, Optional<Object> user) {
		if (userMode == UserMode.LOGOUT) {
			responseHeaders.addHeader(
				"Set-Cookie", 
				SESSION_COOKIE_NAME + "="
				+ "; HttpOnly"
				+ (basePath == null ? "" : "; Path=" + basePath)
				+ "; SameSite=Strict"
				+ "; Max-Age=" + 0
			);
			spaces.remove(sessionId);
		} else {
			Long expiration = maxAgeInSec;
			if (expiration != null) {
				expiration += ZonedDateTime.now().toEpochSecond();
			}
			spaces.put(sessionId, new Tuple2<>(expiration, new CurrentSession(sessionId, sessionSpace, user)));
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
