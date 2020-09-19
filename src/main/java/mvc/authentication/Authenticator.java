package mvc.authentication;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

import utils.security.Hash;
import utils.security.HashException;

public class Authenticator {

	private final long expirationTime;
	private final TokenType tokenType;
	private final String hashSalt;
	
	private final Hash hasher;
	
	public Authenticator(long expirationTime, TokenType tokenType, String hashSalt) {
		this.expirationTime = expirationTime;
		this.tokenType = tokenType;
		this.hashSalt = hashSalt;
		this.hasher = new Hash("SHA-256");
	}

	public AuthResponse login(String content) throws AuthentizationException {
		try {
		// if (serverSide) {
		//	save content to session
		//  return new AuthResponse(createToken(""), Arrays.asList());
		// } else {
			return new AuthResponse(createToken(content), Arrays.asList());
		// }
		} catch (Exception e) {
			throw new AuthentizationException(e);
		}
	}
	
	public AuthResponse logout(String token) {
		return null;
	}
	
	public AuthResponse refresh(String token) throws AuthentizationException {
		return null;
	}
	
	public Optional<Identity> authenticate(Properties header) {
		try {
			Identity token = parseToken(
				tokenType.getFromValueToToken().apply(
					header.getProperty(
						tokenType.getHeaderName() + ""
					)
				)
			);
			// server side storage update expiration time
			return Optional.of(token);
		} catch (Exception e) {
			// TODO log
			return Optional.empty();
		}
	}
	
	public List<String> getHeaders(Optional<Identity> identity) {
		return tokenType.getCreateHeader().apply(identity);
	}
	
	public long getExpirationTime() {
		return expirationTime;
	}
	
	// TODO test this method
	protected String createToken(String content) throws HashException {
		String random = RandomStringUtils.randomAlphanumeric(50);
		long expired = new Date().getTime() + expirationTime;
		// random + expired + ";" + content.length() + ";"+ con
		String hash = hasher.toHash(createHashingMesasge(random, expired, content));
		return String.format("%s%s;%s;%s%š", random, expired, content.length(), content, hash);
	}
	
	// TODO test this method
	protected Identity parseToken(String token) {
		String random = token.substring(0, 50);
		String[] others = token.substring(50).split(";");
		long expired = Long.parseLong(others[0]);
		int contentLength = Integer.parseInt(others[1]);
		String content = others[2].substring(0, contentLength);
		String hash = others[2].substring(contentLength);
		
		if (!hasher.compare(createHashingMesasge(random, expired, content), hash)) {
			throw new RuntimeException("Token corrupted");
		}
		if (expired > new Date().getTime() + expirationTime) {
			throw new RuntimeException("Token is expired");
		}
		return new Identity(content, random, expired);
	}
	
	private String createHashingMesasge(String random, long expired, String content) {
		return String.format("%s%s%s%s", random, expired, content, hashSalt);
	}
		
}
