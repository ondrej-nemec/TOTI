package toti.templating.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class InLineParserTest {
	
	@Test
	public void testAddVariable() {
		InLineParser inline = new InLineParser();
		VariableParser var = new VariableParser(0);
		
		ParsingSimulator.simulate(inline, "10 < ");
		ParsingSimulator.simulate(var, "number}");
		inline.addVariable(var);
		ParsingSimulator.simulate(inline, "}}");
		assertEquals(
			"10 < Template.escapeVariable(getVariable(()->{"
			+ "Object o0_0=getVariable(\"number\");"
			+ "return o0_0;"
			+ "}))",
			inline.getCalling()
		);
	}

	@Test
	@Parameters(method = "dataParseWorks")
	public void testParseWorks(String text, boolean finished, String expected) {
		InLineParser parser = new InLineParser();
		assertEquals(finished, ParsingSimulator.simulate(parser, text));
		assertEquals(expected, parser.getCalling());
	}
	
	public Object[] dataParseWorks() {
		return new Object[] {
			new Object[] {
				"10 > 10 ? \"red\" : \"blue\"}}",
				true,
				"10 > 10 ? \"red\" : \"blue\""
			},
			new Object[] {
					"10 > 10 ? \"red\" : \"blue\"}",
					false,
					"10 > 10 ? \"red\" : \"blue\""
				},
			new Object[] {
					"10 > 10 ? \"red\" : \"blue\"",
					false,
					"10 > 10 ? \"red\" : \"blue\""
				},
			new Object[] {
					"10 > 10 ? \"{\" : \"}\"}}",
					true,
					"10 > 10 ? \"{\" : \"}\""
				},
		};
	}	
}
