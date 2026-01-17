package toti.lib.common;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.functions.Implode;

public class ImplodeTest {

	@ParameterizedTest
	@MethodSource("dataImplodeReturnsCorrectResult")
	public void testImplodeReturnsCorrectResult(String expected, String glue, String[] data) {
		assertEquals(expected, Implode.implode(glue, data));
	}
	
	public static Object[] dataImplodeReturnsCorrectResult() {
		String[] array = new String[] {"a", "b", "c","d"};
		return new Object[] {
			new Object[] {
				"a b c d", " ", array
			},
			new Object[] {
					"a, b, c, d", ", ", array
			}
		};
	}
	
	@Test
	public void testImplodeReturnsCorrectResult() {
		assertEquals("a, b, c, d", Implode.implode(", ", Arrays.asList("a", "b", "c","d")));
	}
	
}
