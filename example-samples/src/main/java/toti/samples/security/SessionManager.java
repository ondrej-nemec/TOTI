package toti.samples.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import toti.answers.request.Identity;
import toti.answers.request.LoggedUser;
import toti.answers.request.SessionUserProvider;

public class SessionManager implements SessionUserProvider {
	
	private final Map<String, ExampleUser> loggedUsers = new HashMap<>();

	@Override
	public Optional<LoggedUser> getUser(Optional<String> headerToken, Optional<String> cookieToken,
			Optional<String> csrfToken) {
		String token = headerToken.orElse(cookieToken.orElse(""));
		if (loggedUsers.containsKey(token)) {
			return Optional.of(loggedUsers.get(token));
		}
		return Optional.empty();
	}

	@Override
	public void saveUser(Optional<LoggedUser> user) {
		// in this examle, nothing is nessessary here
		// there you can save some data to file, db, etc.
	}

	@Override
	public boolean isAllowed(LoggedUser identity, Map<String, Object> params) {
		// this method is used only in combination with template tag
		return false;
	}
	
	// example of login method
	// THIS IS JUST FOR TEST, NEVER USE SOMETHING LIKE THIS IN YOUR APP
	public boolean login(Identity identity, String username, String password) {
		// one password for all users
		if (!"123".equals(password)) {
			return false;
		}
		switch (username) {
			case "user1":
				identity.login(new ExampleUser("user1", 1000*60, createToken()));
				return true;
			case "user2":
				identity.login(new ExampleUser("user2", 1000*60, createToken()));
				return true;
			case "user3":
				identity.login(new ExampleUser("user2", 1000*60, createToken()));
				return true;
			default:
				return false;
		}
	}
	
	private String createToken() {
		return "token_" + new Random().nextInt();
	}

	public void logout(Identity identity) {
		identity.logout();
		String token = identity.getUser().getHeaderToken()
				.orElse(identity.getUser().getCookieToken().orElse(""));
		loggedUsers.remove(token);
		// there should be some custom code 
	}

}
