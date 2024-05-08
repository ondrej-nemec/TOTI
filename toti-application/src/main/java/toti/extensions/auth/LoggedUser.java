package toti.extensions.auth;

import java.util.Optional;

public interface LoggedUser {
	
	long getExpirationTime();
	
	long getExpirationPeriod();
	
	Optional<String> getCookieToken();
	
	Optional<String> getCsrfToken();
	
	Optional<String> getHeaderToken();
	
}
