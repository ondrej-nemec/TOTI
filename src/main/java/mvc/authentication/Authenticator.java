package mvc.authentication;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

import common.Logger;
import utils.security.Hash;
import utils.security.HashException;

public class Authenticator {

	private final long expirationTime;
	private final String hashSalt;
	private final Logger logger;
	
	private final Hash hasher;
	
	public Authenticator(long expirationTime, String hashSalt, Logger logger) {
		this.expirationTime = expirationTime;
		this.logger = logger;
		this.hashSalt = hashSalt;
		this.hasher = new Hash("SHA-256");
	}

	public String login(String content, Identity identity) throws AuthentizationException {
		try {
			String id = RandomStringUtils.randomAlphanumeric(50);
			String token = createToken(id, content);
			identity.set(content, id, expirationTime, token);
			return token;
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
	
	public void logout(Identity token) {
		// TODO some disabling - maybe memory list of tokens
		token.clear();
	}
	
	public String refresh(Identity identity, String content, Boolean append) throws AuthentizationException {
		return login(content, identity);
	}
	
	public Identity authenticate(Properties header) {
		try {
			String token = null;
			if (header.get("Authorization") != null) {
				String[] vals = header.get("Authorization").toString().split(" ", 2);
				if (vals.length == 2) {
					token = vals[1];
				}
			}
			Identity identity = parseToken(token);
			return identity;
		} catch (Exception e) {
			logger.debug("Missing authenticate header", e);
			return Identity.empty();
		}
	}
	
	public long getExpirationTime() {
		return expirationTime;
	}
	
	// TODO test this method
	protected String createToken(String random, String content) throws HashException {
		long expired = new Date().getTime() + expirationTime;
		// random + expired + ";" + content.length() + ";"+ con
		String hash = hasher.toHash(createHashingMesasge(random, expired, content));
		return String.format("%s%s-%s-%s", random, expired, content, hash);
	}
	
	// TODO test this method
	protected Identity parseToken(String token) throws HashException {
		if (token == null || token.isEmpty()) {
			return Identity.empty();
		}
		String random = token.substring(0, 50);
		String[] others = token.substring(50).split("-");
		long expired = Long.parseLong(others[0]);
		String content = others[1];
		String hash = others[2];		
		if (!hasher.compare(createHashingMesasge(random, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired < new Date().getTime()) {
			throw new RuntimeException("Token is expired");
		}
		return Identity.get(content, random, expired, token);
	}
	
	private String createHashingMesasge(String random, long expired, String content) {
		return String.format("%s%s%s%s", random, expired, content, hashSalt);
	}
		
}
