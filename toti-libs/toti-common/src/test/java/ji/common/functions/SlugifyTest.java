package ji.common.functions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class SlugifyTest {

	@Test
	@Parameters
	public void testTuSlugSlugifyString(String string, String expectedSlug) {
		assertEquals(expectedSlug, Slugify.toSlug(string));
	}
	
	public Object[] parametersForTestTuSlugSlugifyString() {
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
