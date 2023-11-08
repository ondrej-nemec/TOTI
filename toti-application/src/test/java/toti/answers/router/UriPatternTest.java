package toti.answers.router;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.application.Module;

@RunWith(JUnitParamsRunner.class)
public class UriPatternTest {
	
	@Test
	@Parameters(method="dataCreateBase")
	public void testCreateBase(String module, String controller, String action, String expected) {
		UriPattern pattern = new UriPattern(){};
		assertEquals(expected, pattern.createUri(mock(Module.class), Object.class, module, controller, action));
	}
	
	public Object[] dataCreateBase() {
		return new Object[] {
			new Object[] { "m", "c", "a", "/m/c/a" },
			new Object[] { "", "c", "a", "/c/a" },
			new Object[] { null, "c", "a", "/c/a" },
			new Object[] { "m", "", "a", "/m/a" },
			new Object[] { "m", null, "a", "/m/a" },
			new Object[] { "m", "c", "", "/m/c" },
			new Object[] { "m", "c", null, "/m/c" },
			new Object[] { "", "", "", "" },
		};
	}
}
