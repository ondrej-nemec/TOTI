package toti.common.functions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import toti.common.exceptions.HashException;

/**
 * Class wrap java hashing mechanism. 
 * 
 * @author Ondřej Němec
 *
 */
// TODO vzit z edy
public class Hash {
	
	public static Hash getSha516() {
		return new Hash("SHA-516");
	}
	
	public static Hash getSha256() {
		return new Hash("SHA-256");
	}
	
	public static Hash getMD5() {
		return new Hash("MD5");
	}
	
	private final String algoritm;
	
	protected Hash(String algoritm) {
		this.algoritm = algoritm;
	}

	public String toHash(String message, String salt) throws HashException {
		try {
			MessageDigest digest = MessageDigest.getInstance(algoritm);
			digest.update(salt.getBytes());
			byte[] hash = Base64.getEncoder().encode(
				digest.digest(
					message.getBytes(StandardCharsets.UTF_8)
				)
			);
			return new String(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new HashException(e);
		}
	}
/*
	private byte[] createSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
	      //Always use a SecureRandom generator for random salt
	      SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
	      //Create array for salt
	      byte[] salt = new byte[16];
	      //Get a random salt
	      sr.nextBytes(salt);
	      //return salt
	      return salt;
	}
*/
	public boolean compare(String message, String hash, String salt) {
		try {
			return hash.equals(toHash(message, salt));
		} catch (HashException e) {
			return false;
		}
	}

}
