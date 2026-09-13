package toti.core.answers.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.core.answers.Headers;
import toti.core.application.register.Register;
import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.MapInit;
import toti.lib.common.tests.TestCase;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;

public class IdentityTest {

	class TestObject {}
	static class Ext1 implements Extension {
		@Override public String getIdentifier() { return "Ext1"; }
		@Override public void init(Env appEnv, Register register) {}
		@Override public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody) {}
		@Override public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}
		@Override public void onApplicationStart() throws Exception {}
		@Override public void onApplicationStop() throws Exception {}
	}
	static class Ext2 implements Extension {
		@Override public String getIdentifier() { return "Ext2"; }
		@Override public void init(Env appEnv, Register register) {}
		@Override public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders, MapDictionary<String> queryParams, RequestParameters requestBody) {}
		@Override public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}
		@Override public void onApplicationStart() throws Exception {}
		@Override public void onApplicationStop() throws Exception {}		
	}

	@Test
	public void testScope() {
		Identity identity = new Identity(
			"", new HashMap<>(), Optional.empty(), Optional.empty(), Optional.empty(), false
		);

		assertNull(identity.getScope(TestObject.class));
		
		TestObject objectA = new TestObject();
		TestObject objectB = new TestObject();

		identity.setScope(objectA);
		assertSame(objectA, identity.getScope(TestObject.class));
		assertNotSame(objectB, identity.getScope(TestObject.class));

		identity.setScope(objectB);
		assertSame(objectB, identity.getScope(TestObject.class));
		assertNotSame(objectA, identity.getScope(TestObject.class));

		identity.removeScope(TestObject.class);
		assertNull(identity.getScope(TestObject.class));
	}

	@ParameterizedTest
	@MethodSource
	public void testSessionSpaces(String message, Map<String, MapDictionary<String>> sessionSpaces, Consumer<Identity> test) {
		Identity identity = new Identity(
			"", sessionSpaces, Optional.empty(), Optional.empty(), Optional.empty(), false
		);
		test.accept(identity);
	}

	public static Object[] testSessionSpaces() {
		return new Object[] {
			new Object[] {
				"Empty spaces",
				new HashMap<>(),
				TestCase.consumer(Identity.class, identity->{
					assertEquals(new HashMap<>(), identity.getSessionSpaces());

					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace());
					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace(new Ext1()));
					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace(new Ext2()));
					assertEquals(Arrays.asList(), identity.getFlashMessages());

					assertEquals(
						MapInit.create()
						.append("Ext1", MapInit.create().toDictionaryMap())
						.append("Ext2", MapInit.create().toDictionaryMap())
						.append("", MapInit.create().toDictionaryMap())
						.append("toti_flash", MapInit.create()
							.append("flash", Arrays.asList())
						.toDictionaryMap())
						.toMap(),
						identity.getSessionSpaces()
					);
				})
			},
			new Object[] {
				"Not empty spaces",
				new MapInit<String, MapDictionary<String>>()
				.append("Ext1", MapInit.create().append("a", "A").toDictionaryMap())
				.append("Ext2", MapInit.create().append("b", "B").toDictionaryMap())
				.append("", MapInit.create().append("c", "C").append("d", "D").toDictionaryMap())
				.append("toti_flash", MapInit.create()
					.append("flash", new LinkedList<>(Arrays.asList(
						new FlashMessage("a", "1"),
						new FlashMessage("b", "2")
					)))
				.toDictionaryMap())
				.toMap(),
				TestCase.consumer(Identity.class, identity->{
					assertEquals(
						MapInit.create().append("c", "C").append("d", "D").toDictionaryMap(),
						identity.getSessionSpace()
					);
					assertEquals(
						MapInit.create().append("a", "A").toDictionaryMap(),
						identity.getSessionSpace(new Ext1())
					);
					assertEquals(
						MapInit.create().append("b", "B").toDictionaryMap(),
						identity.getSessionSpace(new Ext2())
					);
					assertEquals(Arrays.asList(
						new FlashMessage("a", "1"),
						new FlashMessage("b", "2")
					), identity.getFlashMessages());

					assertEquals(
						MapInit.create()
						.append("Ext1", MapInit.create().append("a", "A").toDictionaryMap())
						.append("Ext2", MapInit.create().append("b", "B").toDictionaryMap())
						.append("", MapInit.create().append("c", "C").append("d", "D").toDictionaryMap())
						.append("toti_flash", MapInit.create()
							.append("flash", Arrays.asList())
						.toDictionaryMap())
						.toMap(),
						identity.getSessionSpaces()
					);
				})
			},
			new Object[] {
				"Add to user space",
				new HashMap<>(),
				TestCase.consumer(Identity.class, identity->{
					assertEquals(new HashMap<>(), identity.getSessionSpaces());

					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace());
					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace(new Ext1()));
					assertEquals(MapDictionary.hashMap(), identity.getSessionSpace(new Ext2()));
					assertEquals(Arrays.asList(), identity.getFlashMessages());

					identity.getSessionSpace().put("a", "A");
					identity.getSessionSpace().put("b", "B");

					assertEquals(
						MapInit.create()
						.append("a", "A")
						.append("b", "B")
						.toDictionaryMap(),
						identity.getSessionSpace()
					);

					identity.getSessionSpace().put("a", "AX");
					identity.getSessionSpace().remove("b");

					assertEquals(
						MapInit.create().append("a", "AX").toDictionaryMap(),
						identity.getSessionSpace()
					);
				})
			},
			new Object[] {
				"Flash messages",
				new HashMap<>(),
				TestCase.consumer(Identity.class, identity->{
					assertEquals(new HashMap<>(), identity.getSessionSpaces());

					assertEquals(Arrays.asList(), identity.getFlashMessages());

					identity.addFlashMessage("e1", "M1");
					identity.addFlashMessage("e2", "M2");
					identity.addFlashMessage("e3", "M3");

					var flashes = Arrays.asList(
						new FlashMessage("e1", "M1"),
						new FlashMessage("e2", "M2"),
						new FlashMessage("e3", "M3")
					);
					assertEquals(
						MapInit.create()
						.append("toti_flash", MapInit.create()
						.append("flash", flashes)
						.toDictionaryMap()).toMap(),
						identity.getSessionSpaces()
					);
					assertEquals(flashes, identity.getFlashMessages());


					assertEquals(
						MapInit.create()
						.append("toti_flash", MapInit.create()
							.append("flash", Arrays.asList())
						.toDictionaryMap())
						.toMap(),
						identity.getSessionSpaces()
					);
					assertEquals(Arrays.asList(), identity.getFlashMessages());
				})
			}
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testUserMode(
		String message, Optional<String> sessionId, Optional<Object> user,
		UserMode expectedUserMode, boolean expectedAnonymous, boolean expectedUserPresent
	) {
		Identity identity = new Identity(
			"", new HashMap<>(), sessionId, Optional.empty(), user, false
		);
		assertEquals(expectedUserMode, identity.getUserMode());
		assertEquals(expectedAnonymous, identity.isAnonymous());
		assertEquals(expectedUserPresent, identity.isUserPresent());
	}

	public static Object[] testUserMode() {
		return new Object[] {
			new Object[] {
				"No session id, no user", Optional.empty(), Optional.empty(), UserMode.ANONYMOUS, true, false
			},
			new Object[] {
				"No session id, with user", Optional.empty(), Optional.of(1), UserMode.ANONYMOUS, true, true
			},
			new Object[] {
				"With session id, no user", Optional.of("aaa"), Optional.empty(), UserMode.NOT_LOGGED_USER, false, false
			},
			new Object[] {
				"With session id, with user", Optional.of("aaa"), Optional.of(1), UserMode.LOGGED_USER, false, true
			}
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testUser(String message, Optional<Object> user, Consumer<Identity> test) {
		Identity identity = new Identity(
			"", new HashMap<>(), Optional.of("aaa"), Optional.empty(), user, false
		);
		test.accept(identity);
	}

	public static Object[] testUser() {
		return new Object[] {
			new Object[] {
				"Logout on no user",
				Optional.empty(),
				TestCase.consumer(Identity.class, identity->{
					assertFalse(identity.isUserPresent());
					assertEquals(UserMode.NOT_LOGGED_USER, identity.getUserMode());

					identity.logout();

					assertFalse(identity.isUserPresent());
					assertEquals(UserMode.ANONYMOUS, identity.getUserMode());
				})
			},
			new Object[] {
				"Logout user",
				Optional.of(123),
				TestCase.consumer(Identity.class, identity->{
					assertTrue(identity.isUserPresent());
					assertEquals(UserMode.LOGGED_USER, identity.getUserMode());
					assertEquals(123, identity.getUser(Integer.class));

					identity.logout();
					
					assertFalse(identity.isUserPresent());
					assertNull(identity.getUser(Integer.class));
					assertEquals(UserMode.ANONYMOUS, identity.getUserMode());
				})
			},
			new Object[] {
				"Login with user",
				Optional.of(123),
				TestCase.consumer(Identity.class, identity->{
					assertTrue(identity.isUserPresent());
					assertEquals(123, identity.getUser(Integer.class));
					assertEquals(UserMode.LOGGED_USER, identity.getUserMode());

					identity.login(321);
					
					assertTrue(identity.isUserPresent());
					assertEquals(321, identity.getUser(Integer.class));
					assertEquals(UserMode.LOGGED_USER, identity.getUserMode());
				})
			},
			new Object[] {
				"Login without user",
				Optional.empty(),
				TestCase.consumer(Identity.class, identity->{
					assertFalse(identity.isUserPresent());
					assertNull(identity.getUser(Integer.class));
					assertEquals(UserMode.NOT_LOGGED_USER, identity.getUserMode());

					identity.login(321);
					
					assertTrue(identity.isUserPresent());
					assertEquals(321, identity.getUser(Integer.class));
					assertEquals(UserMode.LOGGED_USER, identity.getUserMode());
				})
			}
		};
	}

}
