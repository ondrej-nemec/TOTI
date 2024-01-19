package toti.answers.request;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class IdentityTest {
	
	class User implements LoggedUser {
		Optional<String> cookie;
		Optional<String> csrf;
		Optional<String> header;
		public User(Optional<String> cookie, Optional<String> csrf, Optional<String> header) {
			this.cookie = cookie;
			this.csrf = csrf;
			this.header = header;
		}
		@Override public long getExpirationTime() { return 0; }
		@Override public long getExpirationPeriod() { return 0; }
		@Override public Object getId() { return null; }
		@Override public Optional<String> getCookieToken() { return cookie; }
		@Override public Optional<String> getCsrfToken() { return csrf; }
		@Override public Optional<String> getHeaderToken() { return header; }
	}
	
	@Test
	@Parameters(method="dataSetUserSelectCorrectMode")
	public void testSetUserSelectCorrectMode(LoggedUser user, AuthMode mode) {
		Identity identity = new Identity("IP", null);
		identity.setUser(user);
		assertEquals(mode, identity.getLoginMode());
	}

	public Object[] dataSetUserSelectCorrectMode() {
		return new Object[] {
			new Object[] {
				new User(Optional.empty(), Optional.empty(), Optional.empty()), AuthMode.NO_TOKEN
			},
			new Object[] {
				new User(Optional.of("cookie"), Optional.empty(), Optional.empty()), AuthMode.COOKIE
			},
			new Object[] {
				new User(Optional.empty(), Optional.of("csrf"), Optional.empty()), AuthMode.NO_TOKEN
			},
			new Object[] {
				new User(Optional.of("cookie"), Optional.of("csrf"), Optional.empty()), AuthMode.COOKIE_AND_CSRF
			},
			new Object[] {
				new User(Optional.empty(), Optional.empty(), Optional.of("header")), AuthMode.HEADER
			},
			new Object[] {
				new User(Optional.empty(), Optional.of("csrf"), Optional.of("header")), AuthMode.HEADER
			},
			new Object[] {
				new User(Optional.of("cookie"), Optional.empty(), Optional.of("header")), AuthMode.HEADER
			},
			new Object[] {
				new User(Optional.of("cookie"), Optional.of("csrf"), Optional.of("header")), AuthMode.HEADER
			},
		};
	}
	
}
