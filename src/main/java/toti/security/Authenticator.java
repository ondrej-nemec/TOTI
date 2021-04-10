package toti.security;

import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

import common.Logger;
import toti.authentication.AuthentizationException;
import utils.security.Hash;

public class Authenticator {

	private final long expirationTime;
	private final String tokenSalt;
	private final Logger logger;
	private final Hash hasher;
	
	public Authenticator(long expirationTime, String tokenSalt, Logger logger) {
		this(expirationTime, tokenSalt, new Hash("SHA-256"), logger);
	}
	
	protected Authenticator(long expirationTime, String tokenSalt, Hash hasher, Logger logger) {
		this.expirationTime = expirationTime;
		this.tokenSalt = tokenSalt;
		this.logger = logger;
		this.hasher = hasher;
	}
	
	public void login(String content, Identity identity) throws AuthentizationException {
		getToken(content, identity);
	}
	
	public void refresh(Identity identity) throws AuthentizationException {
		getToken(identity.getToken(), identity);
	}
	
	public void logout(Identity identity) {
		// TODO file
		identity.clear();
	}
	
	private void getToken(String content, Identity identity) throws AuthentizationException {
		try {
			if (content == null) {
				content = "";
			}
			long now = new Date().getTime();
			String id = RandomStringUtils.randomAlphanumeric(30);
			String random = RandomStringUtils.randomAlphanumeric(50);
			long expired = now + expirationTime;
			String token = createToken(random, id, now, identity.getContent(), expired);
			identity.loginUser(token, id, expired, content);
			// TODO file
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

	// TODO test this method
	protected String createToken(String random, String id, long now, String customData, long expired) {
		String content = new String(Base64.getEncoder().encode(customData.getBytes()));
		String hash = createHashMessage(random, id, expired, content);
		return String.format("%s%s%s%s%s", hash, random, id, expired, content);
	}

	// TODO test this method
	protected void authenticate(Identity identity, long now) {
		String token = identity.getToken();
		if (token == null || token.isEmpty()) {
			return;
		}
		String hash = token.substring(0, 44);
		String random = token.substring(44, 94);
		String id = token.substring(94, 124);
		long expired = Long.parseLong(token.substring(124, 138)); // length == 13, OK until Sat Nov 20 18:46:39 CET 2286
		String content = new String(Base64.getMimeDecoder().decode(token.substring(138).getBytes()));
		// TODO file
		if (!hasher.compare(createHashMessage(random, id, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired < now) {
			throw new RuntimeException("Token is expired");
		}
		identity.setUser(id, expired, content);
	}

	// token: hash(43), random(50), id(30), expired(13), content
	private String createHashMessage(String random, String id, long expired, String content) {
		return String.format("%s%s%s%s%s", random, id, expired, content, tokenSalt);
	}
/*
	public long getExpirationTime() {
		return expirationTime;
	}
*/
}
