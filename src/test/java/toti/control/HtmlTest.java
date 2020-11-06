package toti.control;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.control.Html;

@RunWith(JUnitParamsRunner.class)
public class HtmlTest {

	@Test
	@Parameters(method = "dataReplaceItemValuesReturnsCorrectResult")
	public void testReplaceItemValuesReturnsCorrectResult(String text, String expected) {
		assertEquals(expected, Html.replaceItemValues(text));
	}
	
	public Object[] dataReplaceItemValuesReturnsCorrectResult() {
		return new Object[] {
			new Object[] {"${name}", "\"+(item.name)+\""},
			new Object[] {"${ name }", "\"+(item.name)+\""},
			new Object[] {"${name123}", "\"+(item.name123)+\\\""},
			new Object[] {"${name_123  }", "\"+(item.name_123)+\""},
			new Object[] {"${name-123}", "\"+(item.name-123)+\""},
			new Object[] {"name", "name"},
			new Object[] {"$name", "$name"},
			new Object[] {"${name", "${name"},
			new Object[] {"${na me}", "${na me}"},
			new Object[] {"", ""},
			new Object[] {"some text ${name}", "some text \"+(item.name)+\""},
			new Object[] {"${name} ${attribute}", "\"+(item.name)+\" \"+(item.attribute)+\""},
			new Object[] {"${name} ${attribute} ${name}", "\"+(item.name)+\" \"+(item.attribute)+\" \"+(item.name)+\""},
		};
	}
	
	@Test
	@Parameters(method="dataIsTextSave")
	public void testIsTextSave(String text, boolean expected) {
		Html html = Html.element("");
		assertEquals(expected, html.isTextSave(text));
	}
	
	public Object[] dataIsTextSave() {
		return new Object[] {
			new Object[] {"someText", true},
			new Object[] {"some-text", true},
			new Object[] {"some_text", true},
			new Object[] {"some-text123", true},
			new Object[] {"some text", false},
			new Object[] {"some?text", false},
			new Object[] {"some:text", false},
			new Object[] {" someText ", false},
		};
	}
	
}
