package toti.security;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

import ji.common.Logger;
import ji.common.structures.ThrowingFunction;
import toti.authentication.AuthentizationException;
import ji.common.functions.Hash;
import ji.common.exceptions.HashException;
import ji.common.exceptions.LogicException;

public class Authenticator {

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
			String random = RandomStringUtils.randomAlphanumeric(50);
			long expired = now + expirationTime;
			String token = createToken(random, id);
			cache.save(id, expired, user);
			identity.loginUser(token, id, expirationTime, user);
			return new SecurityToken(token, expired);
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
/*
	public String refresh(Identity identity) throws AuthentizationException {
		return getToken(identity.getUser(), identity);
	}
	
	private String getToken(User user, Identity identity) throws AuthentizationException {
		try {
			long now = new Date().getTime();
			String id = identity.isAnonymous() ? RandomStringUtils.randomAlphanumeric(30) : identity.getId();
			String random = RandomStringUtils.randomAlphanumeric(50);
			long expired = now + expirationTime;
			String token = createToken(random, id, now, expired);
			if (identity.isAnonymous()) {
				cache.save(id, expired, user);
			} else {
				cache.refresh(id, expired);
			}
			identity.loginUser(token, id, expirationTime, user);
			return token;
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
*/
	public void logout(Identity identity) {
		try {
			cache.delete(identity.getId());
			identity.clear();
		} catch (Exception e) {
			logger.warn("Problem with log out", e);
		}
	}
	
	public void authenticate(Identity identity) {
		try {
			authenticate(identity, new Date().getTime());
		} catch (Exception e) {
			logger.debug("Problem with authentication", e);
		}
	}

	protected void authenticate(Identity identity, long now) throws Exception {
		String token = identity.getToken();
		if (token == null || token.isEmpty()) {
			return;
		}
		String hash = token.substring(0, 44);
		String random = token.substring(44, 94);
		String id = token.substring(94, 124);
		//long expired = Long.parseLong(token.substring(124, 137)); // length == 13, OK until Sat Nov 20 18:46:39 CET 2286
		if (!hasher.compare(createHashMessage(random, id/*, expired, content*/), hash)) {
			throw new RuntimeException("Token corrupted");
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
			identity.setUser(id, expirationTime, user);
		}
	}

	// TODO add content - will contains a) data for service from registr, b) serialized, crypted user
	protected String createToken(String random, String id) throws HashException {
		String hash = hasher.toHash(createHashMessage(random, id));
		return String.format("%s%s%s", hash, random, id);
	}

	// token: hash(43), random(50), id(30)
	private String createHashMessage(String random, String id) {
		return String.format("%s%s%s", random, id, tokenSalt);
	}

	public long getExpirationTime() {
		return expirationTime;
	}

}
