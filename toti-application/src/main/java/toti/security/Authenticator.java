package toti.security;

import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

import org.apache.commons.lang3.RandomStringUtils;

import org.apache.logging.log4j.Logger;
import toti.authentication.AuthentizationException;
import ji.common.functions.Hash;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.common.exceptions.HashException;
import ji.common.exceptions.LogicException;

public class Authenticator {
	
	public static final String CSRF_TOKEN_PARAMETER = "_csrf_token";
	private static final String CSRF_TOKEN = "CSRF-TOKEN";
	private static final String AUTH_TOKEN = "AUTH-TOKEN";

	private final long expirationTime;
	private final String tokenSalt;
	private final Logger logger;
	private final Hash hasher;
	private final AuthenticationCache cache;
	
	public Authenticator(
			long expirationTime,
			String tokenSalt,
			AuthenticationCache cache,
			Hash hasher,
			Logger logger) {
		this.expirationTime = expirationTime;
		this.tokenSalt = tokenSalt;
		this.logger = logger;
		this.hasher = hasher;
		this.cache = cache;
	}
	
	public SecurityToken login(User user, Identity identity) throws AuthentizationException {
		if (identity.isPresent()) {
			throw new LogicException("Somebody is allready logged in. " + identity.getUser());
		}
		try {
			long now = new Date().getTime();
			String id = RandomStringUtils.randomAlphanumeric(30);
			String authToken = createToken(RandomStringUtils.randomAlphanumeric(50), id, AUTH_TOKEN);
			String csrfToken = createToken(RandomStringUtils.randomAlphanumeric(50), id, CSRF_TOKEN);
			
			long expired = now + expirationTime;
			cache.save(id, expired, user, csrfToken);
			
			identity.loginUser(authToken, csrfToken, id, expirationTime, user);
			return new SecurityToken(authToken, expirationTime);
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}

	public void logout(Identity identity) {
		try {
			cache.delete(identity.getId());
			identity.clear();
		} catch (Exception e) {
			logger.warn("Problem with log out", e);
		}
	}
	
	public void authenticate(Identity identity, RequestParameters parameters) {
		try {
			authenticate(identity, parameters, new Date().getTime());
		} catch (Exception e) {
			logger.debug("Problem with authentication", e);
		}
	}

	protected void authenticate(Identity identity, RequestParameters parameters, long now) throws Exception {
		String token = identity.getToken();
		if (token == null || token.isEmpty()) {
			return;
		}
		String id = validateToken(token, AUTH_TOKEN);
		if (id == null) {
			throw new RuntimeException("Token corrupted");
		}
		String csrfToken = null;
		if (parameters.containsKey(CSRF_TOKEN_PARAMETER) && validateToken(parameters.getString(CSRF_TOKEN_PARAMETER), CSRF_TOKEN) != null) {
			csrfToken = parameters.getString(CSRF_TOKEN_PARAMETER);
			parameters.remove(CSRF_TOKEN_PARAMETER);
		}
		
		Long expired = cache.getExpirationTime(id);
		if (expired == null) {
			identity.clear();
			throw new RuntimeException("Unknown token");
		}
		if (expired < now) {
			cache.delete(id);
			identity.clear();
			throw new RuntimeException("Token is expired");
		}
		User user = cache.get(id);
		if (user == null) {
			logger.warn("Session id " + id + " is not in active tokens.");
			identity.clear();
		} else {
			cache.refresh(id, now + expirationTime);
			identity.setUser(id, cache.getCsrfToken(id), expirationTime, user, cache.validateCsrfToken(id, csrfToken));
		}
	}
	
	public void saveIdentity(Identity identity) throws IOException {
		if (identity.hasUserChange()) {
			cache.refresh(identity.getId(), identity.getUser());
		}
	}

	public long getExpirationTime() {
		return expirationTime;
	}
	
	public void forEach(Function<User, User> function) {
        cache.forEach(function);
    }
	
	/******** TOKEN *****/
	
	private String validateToken(String token, String type) {
		String hash = token.substring(0, 44);
		String random = token.substring(44, 94);
		String id = token.substring(94, 124);
		//long expired = Long.parseLong(token.substring(124, 137)); // length == 13, OK until Sat Nov 20 18:46:39 CET 2286
		if (!hasher.compare(createHashMessage(random, id/*, expired, content*/, type), hash, tokenSalt)) {
			return null;
		}
		return id;
	}

	// TODO add content - will contains a) data for service from registr, b) serialized, crypted user
	protected String createToken(String random, String id, String type) throws HashException {
		String hash = hasher.toHash(createHashMessage(random, id, type), tokenSalt);
		return String.format("%s%s%s", hash, random, id);
	}

	// token: hash(43), random(50), id(30)
	private String createHashMessage(String random, String id, String type) {
		return String.format("%s%s%s", random, id, type);
	}

}
