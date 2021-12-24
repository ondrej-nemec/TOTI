package toti.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;

import ji.common.Logger;
import ji.common.structures.ThrowingFunction;
import ji.common.functions.Hash;
import ji.common.exceptions.HashException;

public class AuthenticatorTest {

	@Test
	public void testCreateTokenReturnsCorrectTokenWithoutCustomData() throws HashException {
		Hash hash = Mockito.mock(Hash.class);
		Mockito.when(hash.toHash(Mockito.anyString())).thenReturn("hash");
		@SuppressWarnings("unchecked")
		Authenticator auth = new Authenticator(123, "salt", Mockito.mock(ThrowingFunction.class), /* Mockito.mock(AuthenticationCache.class),*/ hash, Mockito.mock(Logger.class));
		assertEquals("hashr@ndomid2000", auth.createToken(
				"r@ndom", 
				"id", 
				1000,
				null, 
				2000
		));
	}
	
	@Test
	// TODO test corrupted token and expired token
	public void testAuthenticateWorks() throws Exception {
		Hash hash = Mockito.mock(Hash.class);
		Mockito.when(hash.toHash(Mockito.anyString())).thenReturn("hash");
		Mockito.when(hash.compare(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		@SuppressWarnings("unchecked")
		Authenticator auth = new Authenticator(123, "salt", Mockito.mock(ThrowingFunction.class), /*Mockito.mock(AuthenticationCache.class),*/ hash, Mockito.mock(Logger.class));
		Identity identity = new Identity("", null, null, 
				// "QlKvbHfY5F4wgrK0tlmrcRImLCx6t59RWq8XvTqmIL4=f1jmBdmnjIgFCEczXFkOYGE7tFulK9pJ1R3EleUauqvMT4WcgMqQqHSXrHW7i8wrFrOLJLHPd2X7Re2D1618244602626"
				"has1has2has3has4has5has6has7has8has9has0hash"
				+ "rand1rand2rand3rand4rand5rand6rand7rand8rand9rand0"
				+ "id1id2id3id4id5id6id7id8id9id0"
				+ "1234567890123",
				false
		);
		auth.authenticate(identity, 1234567890122L);
		// TODO nesedi, protoze id neni obsazeno v aktivnich
		assertEquals("id1id2id3id4id5id6id7id8id9id0", identity.getId());
		assertEquals(1234567890123L, identity.getExpirationTime());
	}
	
}
