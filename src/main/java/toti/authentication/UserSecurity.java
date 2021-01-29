package toti.authentication;

import java.util.function.Function;

import acl.AuthorizationHelper;
import acl.RulesDao;
import acl.structures.AclUser;
import common.Logger;

public class UserSecurity {
	
	private final Logger logger;
	private final String tokenSalt;
	private final long expirationTime;
	private final RulesDao rulesDao;
	private final String redirectUrlNoLoggedUser;
	
	public UserSecurity(
			String redirectUrlNoLoggedUser,
			Function<Identity, AclUser> identityToUser,
			RulesDao rulesDao,
			long expirationTime,
			String tokenSalt, 
			Logger logger) {
		this.redirectUrlNoLoggedUser = redirectUrlNoLoggedUser;
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

	public String getRedirectUrlNoLoggedUser() {
		return redirectUrlNoLoggedUser;
	}

}
