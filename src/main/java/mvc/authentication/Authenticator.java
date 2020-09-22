package mvc.authentication;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

import common.Logger;
import mvc.authentication.storage.Storage;
import utils.security.Hash;
import utils.security.HashException;

public class Authenticator {

	private final long expirationTime;
	private final TokenType tokenType;
	private final String hashSalt;
	private final Storage storage;
	private final Logger logger;
	
	private final Hash hasher;
	
	public Authenticator(long expirationTime, TokenType tokenType, Storage storage, String hashSalt, Logger logger) {
		this.expirationTime = expirationTime;
		this.tokenType = tokenType;
		this.logger = logger;
		this.hashSalt = hashSalt;
		this.hasher = new Hash("SHA-256");
		this.storage = storage;
	}

	public String login(String content, Identity identity) throws AuthentizationException {
		try {
			String id = RandomStringUtils.randomAlphanumeric(50);
			String token;
			if (storage.writeContentToToken()) {
				storage.saveToken(id, expirationTime, content);
				token = createToken(id, content);
			} else {
				token = createToken(id, "");
			}
			identity.set(content, id, expirationTime, token);
			return token;
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
	
	public void logout(Identity token) {
		if (token.isPresent()) {
			storage.deleteToken(token.getId());
		}
		token.clear();
	}
	
	public String refresh(Identity token, String content, Boolean append) throws AuthentizationException {
		storage.updateToken(token.getId(), expirationTime, content, append);
		return "";
	}
	
	public Identity authenticate(Properties header) {
		try {
			String token = tokenType.getHeaderName() + "";
			Identity identity = parseToken(
				tokenType.getFromValueToToken().apply(
					header.getProperty(token)
				)
			);
			if (storage.autoRefresh()) {
				refresh(identity, "", true);
			}
			return identity;
		} catch (Exception e) {
			logger.debug("Missing authenticate header", e);
			return Identity.empty();
		}
	}
	
	public List<String> getHeaders(Identity identity) {
		return tokenType.getCreateHeader().apply(identity);
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
		String random = token.substring(0, 50);
		String[] others = token.substring(50).split("-");
		long expired = Long.parseLong(others[0]);
		String content = others[1];
		String hash = others[2];		
		if (!hasher.compare(createHashingMesasge(random, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired > new Date().getTime() + expirationTime) {
			throw new RuntimeException("Token is expired");
		}
		return Identity.get(content, random, expired, token);
	}
	
	private String createHashingMesasge(String random, long expired, String content) {
		return String.format("%s%s%s%s", random, expired, content, hashSalt);
	}
		
}
