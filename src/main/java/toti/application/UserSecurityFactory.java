package toti.application;

import common.Logger;
import toti.authentication.UserSecurity;

public interface UserSecurityFactory {

	UserSecurity get(long tokenExpirationTime, String tokenSalt, Logger logger);
	
}
