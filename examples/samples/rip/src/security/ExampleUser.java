package toti.samples.security;

import java.util.Optional;

import toti.answers.request.LoggedUser;

public class ExampleUser implements LoggedUser {
	
	private final long expirationTime;
	private final String id;
	private final String token;

	public ExampleUser(String id, long expirationTime, String token) {
		this.expirationTime = expirationTime;
		this.id = id;
		this.token = token;
	}
	
	@Override
	public long getExpirationTime() {
		return expirationTime;
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	public Optional<String> getCookieToken() {
		return Optional.of(token);
	}

	@Override
	public Optional<String> getCsrfToken() {
		return Optional.of(token);
	}

	@Override
	public Optional<String> getHeaderToken() {
		return Optional.of(token);
	}

}
