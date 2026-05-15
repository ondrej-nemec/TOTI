package toti.lib.templating.parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.templating.parsing.enums.VariableSource;

public class VariableParserTest {

	@Test
	public void testParseAddVariableWorks() throws IOException {
		VariableParser first = parseText("title.equals(", 0);
		VariableParser second = parseText("age}", 1);
		first.addVariable(second);
		ParsingSimulator.simulate(first, ")}");
		
		assertEquals(
			"escapeHtml(getVariable(()->{"
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
			+ "}))", 
			first.getCalling(VariableSource.HTML)
		);
		assertTrue(first.escape());
	}

	private VariableParser parseText(String text, int position) {
		VariableParser parser = new VariableParser(position, new ParsingInfo(""));
		ParsingSimulator.simulate(parser, text);
		return parser;
	}

	@ParameterizedTest
	@MethodSource("dataParseTextWorks")
	public void testParseTextWorks(String template, boolean finished, String variableName, String expectedCalling, boolean escape) throws IOException {
		VariableParser parser = new VariableParser(0, new ParsingInfo(""));
		assertEquals(finished, ParsingSimulator.simulate(parser, template));
		assertEquals(variableName, parser.getVariableName());
		assertEquals(expectedCalling, parser.getCalling(VariableSource.HTML));
		assertEquals(escape, parser.escape());
	}
	
	public static Object[] dataParseTextWorks() {
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
				"escapeHtml(getVariable(()->{"
				+ "Object o0_0=getVariable(\"title\");"
				+ "return o0_0;"
				+ "}))",
				true
			},
			new Object[] {
					"title.length()}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
						+ "Object o0_0=getVariable(\"title\");"
						+ "Object o0_1=o0_0.getClass().getMethod(\"length\").invoke(o0_0);"
						+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"title.equals(1)}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_0,1);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,1);"
					+ "}"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"title.class}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"getClass\").invoke(o0_0);"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"age|Integer}",
					true,
					"o0_0",
					"escapeHtml(new DictionaryValue(getVariable(()->{"
					+ "Object o0_0=getVariable(\"age\");"
					+ "return o0_0;"
					+ "})).getValue(Integer.class))",
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
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"get\").invoke(o0_0);"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"map.get(\"value\")}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"value\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"value\");"
					+ "}"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"map.get(12)}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.Integer.class).invoke(o0_0,12);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,12);"
					+ "}"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"map.get(\"}\")}",
					true,
					"o0_1",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"}\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"}\");"
					+ "}"
					+ "return o0_1;"
					+ "}))",
					true
				},
			new Object[] {
					"map.get(\"}\").getBoolean()}",
					true,
					"o0_2",
					"escapeHtml(getVariable(()->{"
					+ "Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"}\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"}\");"
					+ "}"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getBoolean\").invoke(o0_1);"
					+ "return o0_2;"
					+ "}))",
					true
				},
			new Object[] {
				"map.get(\"a\").anotherGet(\"x\")}",
				true,
				"o0_2",
				"escapeHtml(getVariable(()->{"
				+ "Object o0_0=getVariable(\"map\");"
				+ "Object o0_1=null;"
				+ "try{"
				+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"a\");"
				+ "}catch(NoSuchMethodException e){"
				+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"a\");"
				+ "}"
				+ "Object o0_2=null;"
				+ "try{"
				+ "o0_2=o0_1.getClass().getMethod(\"anotherGet\",java.lang.String.class).invoke(o0_1,\"x\");"
				+ "}catch(NoSuchMethodException e){"
				+ "o0_2=o0_1.getClass().getMethod(\"anotherGet\",Object.class).invoke(o0_1,\"x\");"
				+ "}"
				+ "return o0_2;"
				+ "}))",
				true
			}
		};
	}
	
}
