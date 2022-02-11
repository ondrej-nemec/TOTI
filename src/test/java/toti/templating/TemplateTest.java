package toti.templating;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TemplateTest {

	// escaping: https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html

	@Test
	@Parameters(method = "dataEscapeStringWorks")
	public void testEscapeStringWorks(String variable, String expected) {
		assertEquals(expected, Template.escapeHtml(variable));
	}
	
	public Object[] dataEscapeStringWorks() {
		return new Object[] {
			new Object[] {null, "NULL"},
			new Object[] {"no escape", "no escape"},
			new Object[] {"My & comp", "My &amp; comp"},
			new Object[] {"2 < 3", "2 &lt; 3"},
			new Object[] {"3 > 2", "3 &gt; 2"},
			new Object[] {"I said: \"Hello\"", "I said: &#x22Hello&#x22"},
			new Object[] {"I am 'Programmer'", "I am &#x27;Programmer&#x27;"},
			new Object[] {"My & comp -> 'We'", "My &amp; comp -&gt; &#x27;We&#x27;"},
		};
	}
/*
	@Test
	@Parameters(method = "dataEscapeJSWorks")
	public void testEscapJSWorks(String variable, String expected) {
		assertEquals(expected, Template.escapeJs(variable));
	}
	
	public Object[] dataEscapeJSWorks() {
		return new Object[] { // &#xHH;
			new Object[] {null, "NULL"},
			new Object[] {"noescape", "noescape"},
			new Object[] {
				"(some\"' escaped;)", 
			//	"&#123;some&#x22;&#x27;&#x32;escaped&#x59;&#x125;"
				"&#x28;some&#x22;&#x27;&#x20;escaped&#x3b;&#x29;"
			},
		};
	}

	@Test
	@Parameters(method = "dataEscapeCSSWorks")
	public void testEscapeCSSWorks(String variable, String expected) {
		assertEquals(expected, Template.escapeCss(variable));
	}
	
	public Object[] dataEscapeCSSWorks() {
		return new Object[] { // \HH;
			new Object[] {null, ""},
			new Object[] {"noescape", "noescape"},
			new Object[] {
				"(some\"' escaped;)", 
				"\\28some\\22\\27\\20escaped\\3b\\29"
			},
		};
	}

	@Test
	@Parameters(method = "dataEscapeUrlWorks")
	public void testEscapeUrlWorks(String variable, String expected) {
		assertEquals(expected, Template.escapeUrl(variable));
	}
	
	public Object[] dataEscapeUrlWorks() {
		return new Object[] { // %HH;
			new Object[] {null, ""},
			new Object[] {"noescape", "noescape"},
			new Object[] {
				"(some\"' escaped;)", 
				"%28some%22%27%20escaped%3b%29"
			},
		};
	}*/
}
