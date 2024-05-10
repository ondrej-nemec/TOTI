package toti.samples.application;

import java.util.Optional;

import toti.extensions.auth.LoggedUser;

public class SampleUser implements LoggedUser {

	private final String userName;
	private final Optional<String> headerToken;
	private final Optional<String> cookieToken;
	private final Optional<String> csrfToken;
	private final long expiration;
	
	public SampleUser(String userName) {
		this.userName = userName;
		this.headerToken = Optional.of(userName);
		this.cookieToken = Optional.of(userName);
		this.csrfToken = Optional.of(userName);
		this.expiration = 3 * 60 * 1000; // 3 min in nano
	}
	
	public String getUserName() {
		return userName;
	}
	
	@Override
	public long getExpirationTime() {
		return System.currentTimeMillis() * expiration;
	}

	@Override
	public long getExpirationPeriod() {
		return expiration;
	}

	@Override
	public Optional<String> getCookieToken() {
		return cookieToken;
	}

	@Override
	public Optional<String> getCsrfToken() {
		return csrfToken;
	}

	@Override
	public Optional<String> getHeaderToken() {
		return headerToken;
	}

}
