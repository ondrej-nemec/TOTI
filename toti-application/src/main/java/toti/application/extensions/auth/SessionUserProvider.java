package toti.application.extensions.auth;

import java.util.Map;
import java.util.Optional;

public interface SessionUserProvider {

	// before each requerst
	Optional<LoggedUser> getUser(Optional<String> headerToken, Optional<String> cookieToken, Optional<String> csrfToken);
	
	// after each request
	void saveUser(Optional<LoggedUser> user);
	
	// for template
	boolean isAllowed(LoggedUser user, Map<String, Object> params);
	
	String getNotLoggedUserRedirect(String backlink);
	
}
