package toti.authentication;

import java.util.Date;

import logging.LoggerFactory;
import utils.security.Hash;
import utils.security.HashException;

public class AuthenticatorTest {
	
	public static void main(String[] args) throws HashException {
		Authenticator a = new Authenticator(1000, "ewqeqwerrqwerq", LoggerFactory.getLogger("auth"));
		String token = a.createToken(
				"random1234random1234random1234random1234random1234",
				new Date().getTime(),
				"user_nme",
				new Hash("SHA-256")
		);
		System.out.println("Token: " + token);
		a.parseToken(token, true, new Date().getTime(), new Hash("SHA-256"));
	}

}
