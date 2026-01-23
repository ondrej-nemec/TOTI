package toti.lib.common.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SlugifyTest {

	@ParameterizedTest
	@MethodSource("parametersForTestTuSlugSlugifyString")
	public void testTuSlugSlugifyString(String string, String expectedSlug) {
		assertEquals(expectedSlug, Slugify.toSlug(string));
	}
	
	public static Object[] parametersForTestTuSlugSlugifyString() {
		return new Object[] {
			new Object[] {
				"text",
				"text"
			},
			new Object[] {
				"Text Text",
				"text_text"
			},
			new Object[] {
				"ščřž",
				"scrz"
			},
		};
	}
	
}
