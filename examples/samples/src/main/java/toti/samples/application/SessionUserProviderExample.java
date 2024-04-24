package toti.samples.application;

import java.util.Map;
import java.util.Optional;

import toti.answers.request.LoggedUser;
import toti.answers.request.SessionUserProvider;

public class SessionUserProviderExample implements SessionUserProvider {

	@Override
	public Optional<LoggedUser> getUser(Optional<String> headerToken, Optional<String> cookieToken,
		Optional<String> csrfToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUser(Optional<LoggedUser> user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAllowed(LoggedUser identity, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return false;
	}

}
