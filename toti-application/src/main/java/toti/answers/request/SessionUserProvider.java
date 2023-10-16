package toti.answers.request;

import java.util.Optional;

public interface SessionUserProvider {

	Optional<LoggedUser> getUser(Optional<String> headerToken, Optional<String> cookieToken, Optional<String> csrfToken);
	
	void saveUser(Optional<LoggedUser> user);
	
}
