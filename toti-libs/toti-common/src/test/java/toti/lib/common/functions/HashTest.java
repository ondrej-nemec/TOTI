package toti.lib.common.functions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import toti.lib.common.exceptions.HashException;
import toti.lib.common.functions.Hash;

public class HashTest {
	
	@Test
	public void testToHash() throws HashException {
		String origin = "hello world";
		String hashed = "uU0nuZNNPgilLlLX2n2r+sSE7+N6U4DukIj3rOLvzek=";
		Hash hash = new Hash("SHA-256");
		assertEquals(hashed, hash.toHash(origin, ""));
	}

	@Test
	public void testCompare() {
		String origin = "hello world";
		String goodHash =  "uU0nuZNNPgilLlLX2n2r+sSE7+N6U4DukIj3rOLvzek=";
		String wrongHash = "dkjfuJHuKJBNKL184651KJhulfafasklzdfj+kziz=kz";
		
		Hash hash = new Hash("SHA-256");
		
		assertTrue(hash.compare(origin, goodHash, ""));
		assertFalse(hash.compare(origin, wrongHash, ""));
	}
}
