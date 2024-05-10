package toti.samples.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import toti.answers.router.Link;
import toti.extensions.auth.LoggedUser;
import toti.extensions.auth.SessionUserProvider;
import toti.samples.application.controllers.UserController;
/**
* very simple implementation for test
* for this test, token is same as username and all tokens are same
* IT IS NOT SAVE (but enough for test)
*/
public class SessionUserProviderExample implements SessionUserProvider {
	
	private final Link link;
	
	private final Map<String, LoggedUser> loggedUsers = new HashMap<>();
	
	public SessionUserProviderExample(Link link) {
		this.link = link;
	}
	
	public void login(String loginName) {
		this.loggedUsers.put(loginName, new SampleUser(loginName));
	}
	
	public void logout(SampleUser user) {
		this.loggedUsers.remove(user.getUserName());
	}

	@Override
	public Optional<LoggedUser> getUser(Optional<String> headerToken, Optional<String> cookieToken, Optional<String> csrfToken) {
		// need verify all tokens are correct
		// in this case, tokens are correct if all present tokens are same
		String base = headerToken.orElse(cookieToken.orElse(csrfToken.orElse(null)));
		// no token present
		if (base == null) {
			return Optional.empty();
		}
		if (!headerToken.orElse(base).equals(base)) {
			return Optional.empty();
		}
		if (!cookieToken.orElse(base).equals(base)) {
			return Optional.empty();
		}
		if (!csrfToken.orElse(base).equals(base)) {
			return Optional.empty();
		}
		// verify if user is already logged
		if (loggedUsers.containsKey(base)) {
			return Optional.of(loggedUsers.get(base));
		}
		return Optional.empty();
	}

	@Override
	public void saveUser(Optional<LoggedUser> user) {
		// no file or db saving
	}

	@Override
	public boolean isAllowed(LoggedUser identity, Map<String, Object> params) {
		// not required
		return false;
	}

	@Override
	public String getNotLoggedUserRedirect(String backlink) {
		return link.create(UserController.class, c->c.index());
	}

}
