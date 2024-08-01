package ji.common;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.Implode;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ImplodeTest {

	@Test
	@Parameters(method = "dataImplodeReturnsCorrectResult")
	public void testImplodeReturnsCorrectResult(String expected, String glue, String[] data) {
		assertEquals(expected, Implode.implode(glue, data));
	}
	
	public Object[] dataImplodeReturnsCorrectResult() {
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
