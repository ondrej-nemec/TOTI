package toti.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParseObjectTest {
	
	enum Testing {A, B, C}

	@Test
	public void testParseEnum() {
		assertEquals(Testing.B, ParseObject.parse(Testing.class, Testing.B));
		assertEquals(Testing.A, ParseObject.parse(Testing.class, "A"));
	}
	
	// TODO more tests
}
