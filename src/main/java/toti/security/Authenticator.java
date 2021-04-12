package toti.security;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;

import common.Logger;
import toti.authentication.AuthentizationException;
import utils.security.Hash;
import utils.security.HashException;

public class Authenticator {

	private final long expirationTime;
	private final String tokenSalt;
	private final Logger logger;
	private final Hash hasher;
	private final AuthenticationCache cache;
	private final Set<String> activeTokens = new HashSet<>();
	
	public Authenticator(long expirationTime, String tokenSalt, String tempPath, Logger logger) {
		this(expirationTime, tokenSalt, new AuthenticationCache(tempPath), new Hash("SHA-256"), logger);
	}
	
	protected Authenticator(long expirationTime, String tokenSalt, AuthenticationCache cache, Hash hasher, Logger logger) {
		this.expirationTime = expirationTime;
		this.tokenSalt = tokenSalt;
		this.logger = logger;
		this.hasher = hasher;
		this.cache = cache;
		// TODO load active from files, periodically check
	}
	
	public String login(String content, Identity identity) throws AuthentizationException {
		return getToken(content, identity);
	}
	
	public String refresh(Identity identity) throws AuthentizationException {
		return getToken(identity.getContent(), identity);
	}
	
	public void logout(Identity identity) {
		try {
			identity.clear();
			activeTokens.remove(identity.getId());
			cache.delete(identity.getId());
		} catch (Exception e) {
			logger.warn("Problem with log out", e);
		}
	}
	
	private String getToken(String content, Identity identity) throws AuthentizationException {
		try {
			if (content == null) {
				content = "";
			}
			long now = new Date().getTime();
			String id = identity.isAnonymous() ? RandomStringUtils.randomAlphanumeric(30) : identity.getId();
			String random = RandomStringUtils.randomAlphanumeric(50);
			long expired = now + expirationTime;
			String token = createToken(random, id, now, identity.getContent(), expired);
			//activeTokens.remove(identity.get);
			activeTokens.add(id);
			identity.loginUser(token, id, expirationTime, content);
			cache.create(identity);
			return token;
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
	
	public void authenticate(Identity identity) {
		try {
			authenticate(identity, new Date().getTime());
		} catch (Exception e) {
			logger.debug("Missing authenticate header", e);
		}
	}

	protected String createToken(String random, String id, long now, String customData, long expired) throws HashException {
		if (customData == null) {
			customData = "";
		}
		String content = new String(Base64.getEncoder().encode(customData.getBytes()));
		String hash = hasher.toHash(createHashMessage(random, id, expired, content));
		return String.format("%s%s%s%s%s", hash, random, id, expired, content);
	}

	protected void authenticate(Identity identity, long now) throws IOException, HashException {
		String token = identity.getToken();
		if (token == null || token.isEmpty()) {
			return;
		}
		String hash = token.substring(0, 44);
		String random = token.substring(44, 94);
		String id = token.substring(94, 124);
		long expired = Long.parseLong(token.substring(124, 137)); // length == 13, OK until Sat Nov 20 18:46:39 CET 2286
		String content = new String(Base64.getMimeDecoder().decode(token.substring(137).getBytes()));
		if (!hasher.compare(createHashMessage(random, id, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired < now) {
			throw new RuntimeException("Token is expired");
		}
		if (activeTokens.contains(id)) {
			cache.get(id);
			identity.setUser(id, expirationTime, content);
		} else {
			logger.warn("Session id " + id + " is not in active tokens.");
			identity.clear();
		}
	}

	// token: hash(43), random(50), id(30), expired(13), content
	private String createHashMessage(String random, String id, long expired, String content) {
		return String.format("%s%s%s%s%s", random, id, expired, content, tokenSalt);
	}

	public long getExpirationTime() {
		return expirationTime;
	}

}
