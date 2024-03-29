package toti.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Hash;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.common.exceptions.HashException;

public class AuthenticatorTest {

	@Test
	public void testCreateTokenReturnsCorrectTokenWithoutCustomData() throws HashException {
		Hash hash = Mockito.mock(Hash.class);
		Mockito.when(hash.toHash(Mockito.anyString(), Mockito.anyString())).thenReturn("hash");
		Authenticator auth = new Authenticator(123, "salt", Mockito.mock(AuthenticationCache.class), hash, Mockito.mock(Logger.class));
		assertEquals("hashr@ndomid", auth.createToken(
				"r@ndom", 
				"id",
				"AUTH"
		));
	}
	
	@Test
	// TODO test corrupted token and expired token
	// TODO csrf token
	public void testAuthenticateWorks() throws Exception {
		Hash hash = Mockito.mock(Hash.class);
		Mockito.when(hash.toHash(Mockito.anyString(), Mockito.anyString())).thenReturn("hash");
		Mockito.when(hash.compare(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		AuthenticationCache cache = Mockito.mock(AuthenticationCache.class);
		Mockito.when(cache.getExpirationTime("id1id2id3id4id5id6id7id8id9id0")).thenReturn(1234567890132L);
		Mockito.when(cache.get("id1id2id3id4id5id6id7id8id9id0"))
			.thenReturn(new User("id1id2id3id4id5id6id7id8id9id0", null));
		Authenticator auth = new Authenticator(123, "salt", cache, hash, Mockito.mock(Logger.class));
		Identity identity = new Identity("", null, null, 
				// "QlKvbHfY5F4wgrK0tlmrcRImLCx6t59RWq8XvTqmIL4=f1jmBdmnjIgFCEczXFkOYGE7tFulK9pJ1R3EleUauqvMT4WcgMqQqHSXrHW7i8wrFrOLJLHPd2X7Re2D1618244602626"
				"has1has2has3has4has5has6has7has8has9has0hash"
				+ "rand1rand2rand3rand4rand5rand6rand7rand8rand9rand0"
				+ "id1id2id3id4id5id6id7id8id9id0"
				+ "1234567890123",
				AuthMode.COOKIE
		);
		auth.authenticate(identity, new RequestParameters(), 1234567890122L);
		assertEquals("id1id2id3id4id5id6id7id8id9id0", identity.getId());
		assertEquals(123, identity.getExpirationTime()); // 1234567890123L
	}
	
}
