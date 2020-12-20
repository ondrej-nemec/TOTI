package toti.authentication;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;

import common.Logger;
import utils.security.Hash;
import utils.security.HashException;

public class Authenticator {

	private final static String cookieName = "SessionID";
	
	private final Set<String> activeTokens = new HashSet<>();
	
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
		return getToken(content, identity);
	}
	
	public String refresh(Identity identity, String content, Boolean append) throws AuthentizationException {
		return getToken(content, identity);
	}
	
	public void logout(Identity token) {
		activeTokens.remove(token.getToken());
		token.clear();
	}
	
	private String getToken(String content, Identity identity) throws AuthentizationException {
		try {
			String id = RandomStringUtils.randomAlphanumeric(50);
			String token = createToken(id, content);
			identity.set(content, id, expirationTime, token, false); // just for login/refresh request
			activeTokens.remove(identity.getToken());
			activeTokens.add(token);
			return token;
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}

	public List<String> getHeaders(Identity identity) {
		if (identity.isPresent()) {
			return Arrays.asList(
				"Set-Cookie: "
				+ cookieName + "=" + identity.getToken()
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + (expirationTime / 1000)
			);
		}
		return Arrays.asList(
				/*"Set-Cookie: "
				+ cookieName + "=empty"
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=0"*/
			);
	}
	
	public Identity authenticate(Properties header) {
		try {
			String token = null;
			boolean apiAllowed = false;
			if (header.get("Authorization") != null) {
				String[] vals = header.get("Authorization").toString().split(" ", 2);
				if (vals.length == 2) {
					token = vals[1];
					apiAllowed = true;
				}
			} else {
				token = Cookie.getCookieValue(header, cookieName);
			}
			if (!activeTokens.contains(token)) {
				return Identity.empty();
			}
			Identity identity = parseToken(token, apiAllowed);
			return identity;
		} catch (Exception e) {
			logger.debug("Missing authenticate header", e);
			return Identity.empty();
		}
	}
	
	public long getExpirationTime() {
		return expirationTime;
	}
	
	private String createToken(String random, String content) throws HashException {
		return createToken(random, new Date().getTime(), content, hasher);
	}
	
	// TODO test this method
	protected String createToken(String random, long actual, String content, Hash hasher) throws HashException {
		long expired = actual + expirationTime;
		String hash = hasher.toHash(createHashingMesasge(random, expired, content));
		return String.format(
				"%s%s%s-%s", 
				hash,
				random,
				expired,
				new String(Base64.getEncoder().encode(content.getBytes()))
		);
	}
	
	private Identity parseToken(String token, boolean apiAllowed) throws HashException {
		return parseToken(token, apiAllowed, new Date().getTime(), hasher);
	}
	
	// TODO test this method
	protected Identity parseToken(String token, boolean apiAllowed, long now, Hash hasher) throws HashException {
		if (token == null || token.isEmpty()) {
			return Identity.empty();
		}
		String hash = token.substring(0, 44);
		String random = token.substring(44, 94);
		String[] others = token.substring(94).split("-", 2);
		long expired = Long.parseLong(others[0]);
		String content = new String(Base64.getMimeDecoder().decode(others[1].getBytes()));
		if (!hasher.compare(createHashingMesasge(random, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired < now) {
			throw new RuntimeException("Token is expired");
		}
		return Identity.get(content, random, expired, token, apiAllowed);
	}
	
	private String createHashingMesasge(String random, long expired, String content) {
		return String.format("%s%s%s%s", random, expired, content, hashSalt);
	}
		
}
