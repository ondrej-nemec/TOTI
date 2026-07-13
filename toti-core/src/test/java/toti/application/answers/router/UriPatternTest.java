package toti.application.answers.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import toti.application.application.Module;

public class UriPatternTest {
	
	@ParameterizedTest
	@MethodSource("dataCreateBase")
	public void testCreateBase(String module, String controller, String action, String expected) throws NoSuchMethodException, SecurityException {
		UriPattern pattern = new UriPattern(){};
		assertEquals(expected, pattern.createUri(
			mock(Module.class), Object.class, Object.class.getMethod("toString"),
			module, controller, action
		));
	}
	
	public static Object[] dataCreateBase() {
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
