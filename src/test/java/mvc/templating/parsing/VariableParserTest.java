package mvc.templating.parsing;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import mvc.templating.parsing.VariableParser;

@RunWith(JUnitParamsRunner.class)
public class VariableParserTest {

	//TODO test throws
	
	
	@Test
	@Parameters(method = "dataParseTextWorks")
	public void testParseTextWorks(String template, String expectedInit, String expectedResult) throws IOException {		
		VariableParser parser = new VariableParser(0);
		char previous = '\u0000';
		for (char c : template.toCharArray()) {
			parser.parse(c, previous);
			previous = c;
		}
		assertEquals(expectedInit, parser.getInit());
		assertEquals(expectedResult, parser.getResult());
	}
	
	public Object[] dataParseTextWorks() {
		return new Object[] {
			new Object[] {
					"var}",
					"Object o0_1=variables.get(\"var\");",
					"o0_1"
				},
			new Object[] {
					"var.equals(1)}",
					"Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_1,1);",
					"o0_2"
				},
			new Object[] {
					"var.getClass().equals(1)}",
					"Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getClass\").invoke(o0_1);"
					+ "Object o0_3=o0_2.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_2,1);",
					"o0_3"
				},
			new Object[] {
					"var.class.equals(1)}",
					"Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getClass\").invoke(o0_1);"
					+ "Object o0_3=o0_2.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_2,1);",
					"o0_3"
				},/*
			new Object[] {
					"var.equals(${var2})",
					"Object o0_1=variables.get(\"var\");"
					+ "Object o0_1=variables.get(\"var2\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"equals\").invoke(o0_1, 1);",
					"b.append(Template.escapeVariable(o0_2));"
				},*/
		};
	}
	
}
