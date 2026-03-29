package toti.lib.templating.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JavaParserTest {

	@ParameterizedTest
	@MethodSource("dataParseWorks")
	public void testParseWorks(String text, boolean finished, String expected, boolean isReturning) {
		JavaParser parser = new JavaParser();
		assertEquals(finished, ParsingSimulator.simulate(parser, text));
		assertEquals(expected, parser.getContent());
		assertEquals(isReturning, parser.isReturning());
	}
	
	public static Object[] dataParseWorks() {
		return new Object[] {
			new Object[] {
				"%>",
				true,
				"",
				false
			},
			new Object[] {
				"java code here",
				false,
				"java code here",
				false
			},
			new Object[] {
				"java code here%>",
				true,
				"java code here",
				false
			},
			new Object[] {
				"-- comment",
				false,
				"",
				false
			},
			new Object[] {
				"-- comment --%>",
				true,
				"",
				false
			},
			new Object[] {
				"-- comment %>",
				false,
				"",
				false
			},
			new Object[] {
				"- -",
				false,
				"- -",
				false
			},
			new Object[] {
				"% %>",
				true,
				"% ",
				false
			},
			new Object[] {
				"something - another --%>",
				true,
				"something - another --",
				false
			},
			new Object[] {
				"= returning code %>",
				true,
				" returning code ",
				true
			},
			new Object[] {
				" returning = code %>",
				true,
				" returning = code ",
				false
			}
		};
	}
	
}
