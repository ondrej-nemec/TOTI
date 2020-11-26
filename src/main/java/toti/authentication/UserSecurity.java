package toti.authentication;

import java.util.function.Function;

import common.Logger;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import interfaces.RulesDao;

public class UserSecurity {
	
	private final Logger logger;
	private final String tokenSalt;
	private final long expirationTime;
	private final RulesDao rulesDao;
	
	public UserSecurity(
			Function<Identity, AclUser> identityToUser,
			RulesDao rulesDao,
			long expirationTime,
			String tokenSalt, 
			Logger logger) {
		this.logger = logger;
		this.expirationTime = expirationTime;
		this.tokenSalt = tokenSalt;
		this.rulesDao = rulesDao; 
		Identity.TO_USER = identityToUser;
	}
	
	public AuthorizationHelper getAuthorizator() {
		return new AuthorizationHelper(rulesDao, logger);
	}
	
	public Authenticator getAuthenticator() {
		return new Authenticator(
				expirationTime,
				tokenSalt,
				logger
		);
	}

}
