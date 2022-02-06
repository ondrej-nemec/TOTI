package toti.templating.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.parsing.VariableParser;

@RunWith(JUnitParamsRunner.class)
public class VariableParserTest {

	@Test
	public void testParseAddVariableWorks() throws IOException {
		VariableParser first = parseText("title.equals(", 0);
		VariableParser second = parseText("age}", 1);
		first.addVariable(second);
		ParsingSimulator.simulate(first, ")}");
		
		assertEquals(
			"getVariable(()->{"
			+ "Object o0_0=getVariable(\"title\");"
			+ "Object o1_0_aux=getVariable(()->{"
				+ "Object o1_0=getVariable(\"age\");"
				+ "return o1_0;"
			+ "});"
			+ "Object o0_1=null;"
			+ "try{"
			+ "o0_1=o0_0.getClass().getMethod(\"equals\",o1_0_aux.getClass()).invoke(o0_0,o1_0_aux);"
			+ "}catch(NoSuchMethodException e){"
			+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,o1_0_aux);"
			+ "}"
			+ "return o0_1;"
			+ "})", 
			first.getCalling()
		);
		assertTrue(first.escape());
	}

	private VariableParser parseText(String text, int position) {
		VariableParser parser = new VariableParser(position);
		ParsingSimulator.simulate(parser, text);
		return parser;
	}

	@Test
	@Parameters(method = "dataParseTextWorks")
	public void testParseTextWorks(String template, boolean finished, String variableName, String expectedCalling, boolean escape) throws IOException {
		VariableParser parser = new VariableParser(0);
		assertEquals(finished, ParsingSimulator.simulate(parser, template));
		assertEquals(variableName, parser.getVariableName());
		assertEquals(expectedCalling, parser.getCalling());
		assertEquals(escape, parser.escape());
	}
	
	public Object[] dataParseTextWorks() {
		return new Object[] {
		/*	new Object[] {
				"title",
				false,
				"o0_0",
				"getVariable(()->{"
				+ "Object o0_0=getVariable(\"title\");"
				+ "return o0_0;"
				+ "})",
				true
			},*/
			new Object[] {
				"title}",
				true,
				"o0_0",
				"getVariable(()->{"
				+ "Object o0_0=getVariable(\"title\");"
				+ "return o0_0;"
				+ "})",
				true
			},
			new Object[] {
					"title.length()}",
					true,
					"o0_1",
					"getVariable(()->{"
						+ "Object o0_0=getVariable(\"title\");"
						+ "Object o0_1=o0_0.getClass().getMethod(\"length\").invoke(o0_0);"
						+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"title.equals(1)}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_0,1);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,1);"
					+ "}"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"title.class}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"getClass\").invoke(o0_0);"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"age|Integer}",
					true,
					"o0_0",
					"new DictionaryValue(getVariable(()->{"
					+ "Object o0_0=getVariable(\"age\");"
					+ "return o0_0;"
					+ "})).getValue(Integer.class)",
					true
				},
			new Object[] {
					"title|noescape}",
					true,
					"o0_0",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "return o0_0;"
					+ "})",
					false
				},
			new Object[] {
					"title|String|noescape}",
					true,
					"o0_0",
					"new DictionaryValue(getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "return o0_0;"
					+ "})).getValue(String.class)",
					false
				},
			new Object[] {
					"title|noescape|String}",
					true,
					"o0_0",
					"new DictionaryValue(getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "return o0_0;"
					+ "})).getValue(String.class)",
					false
				},
			new Object[] {
					"map.get()}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"get\").invoke(o0_0);"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"map.get(\"value\")}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"value\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"value\");"
					+ "}"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"map.get(12)}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.Integer.class).invoke(o0_0,12);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,12);"
					+ "}"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"map.get(\"}\")}",
					true,
					"o0_1",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"}\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"}\");"
					+ "}"
					+ "return o0_1;"
					+ "})",
					true
				},
			new Object[] {
					"map.get(\"}\").getBoolean()}",
					true,
					"o0_2",
					"getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"}\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"}\");"
					+ "}"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getBoolean\").invoke(o0_1);"
					+ "return o0_2;"
					+ "})",
					true
				}
		};
	}
	
}
