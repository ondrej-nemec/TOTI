package toti.answers.request;

import java.util.Optional;

import ji.common.structures.DictionaryValue;

public interface LoggedUser {
	
	long getExpirationTime();
	
	long getExpirationPeriod();

	Object getId();
	
	default DictionaryValue getUnique() {
		return new DictionaryValue(getId());
	}
	
	Optional<String> getCookieToken();
	
	Optional<String> getCsrfToken();
	
	Optional<String> getHeaderToken();
	
}
