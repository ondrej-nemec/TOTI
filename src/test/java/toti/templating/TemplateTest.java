package toti.templating;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.Template;

@RunWith(JUnitParamsRunner.class)
public class TemplateTest {

	@Test
	@Parameters(method = "dataEscapeStringWorks")
	public void testEscapeStringWorks(String variable, String expected) {
		assertEquals(expected, Template.escapeVariable(variable));
	}
	
	public Object[] dataEscapeStringWorks() {
		return new Object[] {
			new Object[] {"no escape", "no escape"},
			new Object[] {"My & comp", "My &amp; comp"},
			new Object[] {"2 < 3", "2 &lt; 3"},
			new Object[] {"3 > 2", "3 &gt; 2"},
			new Object[] {"I said: \"Hello\"", "I said: &quot;Hello&quot;"},
			new Object[] {"I am 'Programmer'", "I am &apos;Programmer&apos;"},
			new Object[] {"My & comp -> 'We'", "My &amp; comp -&gt; &apos;We&apos;"},
		};
	}
	
}
