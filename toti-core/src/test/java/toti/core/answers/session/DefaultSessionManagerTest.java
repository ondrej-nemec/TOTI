package toti.core.answers.session;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import toti.core.answers.Headers;
import toti.core.answers.session.DefaultSessionManager.Session;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.MapInit;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.tests.TestCase;
import toti.lib.tcpip.structures.RequestParameters;

public class DefaultSessionManagerTest {

	@Test
	public void testRunCheck() {
		ZonedDateTime now = ZonedDateTime.of(2026, 9, 12, 20, 45, 0, 0, ZoneId.systemDefault());
		Map<String, Session> spaces = new MapInit<String, Session>()
		.append("a", new Session("a", null, now.minusSeconds(1).toEpochSecond(), null, null))
		.append("b", new Session("b", null, now.plusSeconds(1).toEpochSecond(), null, null))
		.append("c", new Session("c", null, null, null, null))
		.toMap();
		DefaultSessionManager dsm = new DefaultSessionManager(null, null, spaces);
		dsm.runCheck(now);
		TestCase.assertEquals(
			new MapInit<String, Session>()
			.append("b", new Session("b", null, now.plusSeconds(1).toEpochSecond(), null, null))
			.append("c", new Session("c", null, null, null, null))
			.toMap(),
			spaces
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testCheck(String message, String sessionId, Session expected) {
		ZonedDateTime now = ZonedDateTime.of(2026, 9, 12, 20, 45, 0, 0, ZoneId.systemDefault());
		
		Map<String, Session> spaces = new MapInit<String, Session>()
		.append("a", null)
		.append("b", new Session("b", null, now.minusSeconds(1).toEpochSecond(), null, null))
		.append("c", new Session("c", null, null, null, null))
		.append("d", new Session("d", null, now.plusSeconds(1).toEpochSecond(), null, null))
		.append("e", new Session("e", null, 0L, null, null))
		.toMap();

		DefaultSessionManager dsm = new DefaultSessionManager(null, null, spaces);
		assertEquals(expected, dsm.check(sessionId, now));
	}

	public static Object[] testCheck() {
		return new Object[] {
			new Object[] { "Not existing SessionId", "0", null },
			new Object[] { "Session is null", "a", null },
			new Object[] { "Session is expired", "b", null },
			new Object[] { "Expiration time is null", "c", new Session("c", null, null, null, null) },
			new Object[] { "Session is still active", "d", new Session("d", null, 
				ZonedDateTime.of(2026, 9, 12, 20, 45, 1, 0, ZoneId.systemDefault()).toEpochSecond(), null, null
			) },
			new Object[] { "Expiration is 0", "e", null }
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testRestoreSession(String message, Long maxAgeInSec, Headers headers, RequestParameters body, Optional<CurrentSession> expected) {
		Map<String, Session> spaces = new MapInit<String, Session>()
		.append("a", new Session(
			"a", "aa", null, new MapInit<String, MapDictionary<String>>()
			.append("x", MapDictionary.hashMap())
			.toMap(), Optional.of(10)
		))
		.append("b", new Session("b", null, 0L, null, null))
		.append("c", new Session(
			"c", "cc", null, new MapInit<String, MapDictionary<String>>().toMap(),
			Optional.of(10)
		))
		.toMap();

		DefaultSessionManager dsm = spy(new DefaultSessionManager(null, maxAgeInSec, spaces));
		ObjectBuilder<String> secret = new ObjectBuilder<>("");
		doAnswer(i->{
			secret.set(secret.get() + "0");
			return secret.get();
		}).when(dsm).generateSecret();
		ZonedDateTime now = ZonedDateTime.of(2026, 9, 12, 20, 45, 0, 0, ZoneId.systemDefault());

		Optional<CurrentSession> actual = dsm.restoreSession(headers, MapDictionary.hashMap(), body, now);
		assertEquals(expected, actual);
	}

	public static Object[] testRestoreSession() {
		return new Object[] {
			new Object[] {
				"Token in header",
				3600L,
				new Headers().addHeader("Authorization", "token a"),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"a",
					new MapInit<String, MapDictionary<String>>().append("x", MapDictionary.hashMap()).toMap(),
					Optional.of(10),
					"aa", false
				))
			},
			new Object[] {
				"Token in cookie",
				3600L,
				new Headers().addHeader("Cookie", "SessionID=a"),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"a",
					new MapInit<String, MapDictionary<String>>().append("x", MapDictionary.hashMap()).toMap(),
					Optional.of(10),
					"aa", false
				))
			},
			new Object[] {
				"Header override cookie",
				3600L,
				new Headers().addHeader("Authorization", "token a").addHeader("Cookie", "SessionID=c"),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"a",
					new MapInit<String, MapDictionary<String>>().append("x", MapDictionary.hashMap()).toMap(),
					Optional.of(10),
					"aa", false
				))
			},
			new Object[] {
				"No token | No max age",
				null,
				new Headers(),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"0",
					new MapInit<String, MapDictionary<String>>().toMap(),
					Optional.empty(),
					"00", false
				))
			},
			new Object[] {
				"No token | Max age set",
				3600L,
				new Headers(),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"0",
					new MapInit<String, MapDictionary<String>>().toMap(),
					Optional.empty(),
					"00", false
				))
			},
			new Object[] {
				"Session is expired | No max age",
				null,
				new Headers().addHeader("Authorization", "token b").addHeader("Cookie", "SessionID=b"),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"0",
					new MapInit<String, MapDictionary<String>>().toMap(),
					Optional.empty(),
					"00", false
				))
			},
			new Object[] {
				"Session is expired | Max age set",
				3600L,
				new Headers().addHeader("Authorization", "token b").addHeader("Cookie", "SessionID=b"),
				new RequestParameters(),
				Optional.of(new CurrentSession(
					"0",
					new MapInit<String, MapDictionary<String>>().toMap(),
					Optional.empty(),
					"00", false
				))
			},
			new Object[] {
				"With valid CSRF",
				3600L,
				new Headers().addHeader("Authorization", "token a").addHeader("Cookie", "SessionID=a"),
				new RequestParameters().put("_csrf_token", "aa"),
				Optional.of(new CurrentSession(
					"a",
					new MapInit<String, MapDictionary<String>>().append("x", MapDictionary.hashMap()).toMap(),
					Optional.of(10),
					"aa", true
				))
			},
			new Object[] {
				"With invalid CSRF",
				3600L,
				new Headers().addHeader("Authorization", "token a").addHeader("Cookie", "SessionID=a"),
				new RequestParameters().put("_csrf_token", "a"),
				Optional.of(new CurrentSession(
					"a",
					new MapInit<String, MapDictionary<String>>().append("x", MapDictionary.hashMap()).toMap(),
					Optional.of(10),
					"aa", false
				))
			}
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testSaveSession(String message, UserMode userMode, Long maxAgeInSec, String expectedHeader, Session expectedSession) {
		Map<String, Session> spaces = new MapInit<String, Session>()
		.append("a", new Session(
			"a", "aa", null, new MapInit<String, MapDictionary<String>>()
			.toMap(), Optional.of(10)
		))
		.toMap();

		DefaultSessionManager dsm = spy(new DefaultSessionManager(null, maxAgeInSec, spaces));
		ZonedDateTime now = ZonedDateTime.of(2026, 9, 12, 20, 45, 0, 0, ZoneId.systemDefault());
		
		Headers headers = mock(Headers.class);
		dsm.saveSession(
			headers, Optional.of("a"), new MapInit<String, MapDictionary<String>>().toMap(),
			userMode, Optional.of(12), now
		);

		verify(headers, times(1)).addHeader("Set-Cookie", expectedHeader);
		assertEquals(expectedSession, spaces.get("a"));
		verifyNoMoreInteractions(headers);
	}

	public static Object[] testSaveSession() {
		return new Object[] {
			new Object[] {
				"Anonymous with max time", UserMode.ANONYMOUS, 3600L, "SessionID=; HttpOnly; Path=/; SameSite=Strict; Max-Age=0", null
			},
			new Object[] {
				"Anonymous without max time", UserMode.ANONYMOUS, null, "SessionID=; HttpOnly; Path=/; SameSite=Strict; Max-Age=0", null
			},
			new Object[] {
				"Not logged with max time", UserMode.NOT_LOGGED_USER, 3600L, "SessionID=a; HttpOnly; Path=/; SameSite=Strict; Max-Age=3600",
				new Session(
					"a", "aa", 1789249500L, new MapInit<String, MapDictionary<String>>()
					.toMap(), Optional.of(12)
				)
			},
			new Object[] {
				"Not logged without max time", UserMode.NOT_LOGGED_USER, null, "SessionID=a; HttpOnly; Path=/; SameSite=Strict",
				new Session(
					"a", "aa", null, new MapInit<String, MapDictionary<String>>()
					.toMap(), Optional.of(12)
				)
			},
			new Object[] {
				"Logged with max time", UserMode.LOGGED_USER, 3600L, "SessionID=a; HttpOnly; Path=/; SameSite=Strict; Max-Age=3600",
				new Session(
					"a", "aa", 1789249500L, new MapInit<String, MapDictionary<String>>()
					.toMap(), Optional.of(12)
				)
			},
			new Object[] {
				"Logged without max time", UserMode.LOGGED_USER, null, "SessionID=a; HttpOnly; Path=/; SameSite=Strict",
				new Session(
					"a", "aa", null, new MapInit<String, MapDictionary<String>>()
					.toMap(), Optional.of(12)
				)
			}
		};
	}

}
