package toti.templating.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class JavaParserTest {

	@Test
	@Parameters(method="dataParseWorks")
	public void testParseWorks(String text, boolean finished, String expected) {
		JavaParser parser = new JavaParser();
		assertEquals(finished, ParsingSimulator.simulate(parser, text));
		assertEquals(expected, parser.getContent());
	}
	
	public Object[] dataParseWorks() {
		return new Object[] {
			new Object[] {
				"%>",
				true,
				""
			},
			new Object[] {
				"java code here",
				false,
				"java code here"
			},
			new Object[] {
				"java code here%>",
				true,
				"java code here"
			},
			new Object[] {
				"-- comment",
				false,
				""
			},
			new Object[] {
				"-- comment --%>",
				true,
				""
			},
			new Object[] {
				"-- comment %>",
				false,
				""
			},
			new Object[] {
				"- -",
				false,
				"- -"
			},
			new Object[] {
				"% %>",
				true,
				"% "
			},
			new Object[] {
				"something - another --%>",
				true,
				"something - another --"
			}
		};
	}
	
}
