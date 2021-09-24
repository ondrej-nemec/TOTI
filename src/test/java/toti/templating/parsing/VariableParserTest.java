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

	// TODO add throwing cases
	
	@Test
	public void testParseAddVariableWorks() throws IOException {
		VariableParser first = parseText("title.equals(", 0);
		VariableParser second = parseText("age", 1);
		parseText(first, ")");
		first.addVariable(second);
		
		assertEquals(
			"Object o0_0=getVariable(\"title\");"
			+ "Object o1_0=getVariable(\"age\");"
			+ "Object o0_1=null;"
			+ "try{"
			+ "o0_1=o0_0.getClass().getMethod(\"equals\",o1_0.getClass()).invoke(o0_0,o1_0);"
			+ "}catch(NoSuchMethodException e){"
			+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,o1_0);"
			+ "}", 
			first.getDeclare()
		);
		assertEquals("o0_1", first.getCalling());
		assertTrue(first.escape());
	}

	private VariableParser parseText(String text, int position) {
		return parseText(new VariableParser(position), text);
	}
	
	private VariableParser parseText(VariableParser parser, String text) {
		char previous = '\u0000';
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		for (char c : text.toCharArray()) {
			if (c == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
			} else if (c == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
			}
			parser.accept(previous, c, isSingleQuoted, isDoubleQuoted);
			previous = c;
		}
		return parser;
	}

	@Test
	@Parameters(method = "dataParseTextWorks")
	public void testParseTextWorks(String template, String expectedDeclare, String expectedCalling, boolean escape) throws IOException {
		VariableParser parser = parseText(template, 0);
		assertEquals(expectedDeclare, parser.getDeclare());
		assertEquals(expectedCalling, parser.getCalling());
		assertEquals(escape, parser.escape());
	}
	
	public Object[] dataParseTextWorks() {
		return new Object[] {
			new Object[] {
				"title",
				"Object o0_0=getVariable(\"title\");",
				"o0_0", true
			},
			new Object[] {
					"title.length()",
					"Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"length\").invoke(o0_0);",
					"o0_1", true
				},
			new Object[] {
					"title.equals(1)",
					"Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",java.lang.Integer.class).invoke(o0_0,1);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,1);"
					+ "}",
					"o0_1", true
				},
			new Object[] {
					"title.class",
					"Object o0_0=getVariable(\"title\");"
					+ "Object o0_1=o0_0.getClass().getMethod(\"getClass\").invoke(o0_0);",
					"o0_1", true
				},
			new Object[] {
					"age|Integer",
					"Object o0_0=getVariable(\"age\");",
					"new common.structures.DictionaryValue(o0_0).getValue(Integer.class)",
					true
				},
			new Object[] {
					"title|noescape",
					"Object o0_0=getVariable(\"title\");",
					"o0_0", false
				},
			new Object[] {
					"title|String|noescape",
					"Object o0_0=getVariable(\"title\");",
					"new common.structures.DictionaryValue(o0_0).getValue(String.class)", false
				},
			new Object[] {
					"map.get(\"value\")",
					"Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.String.class).invoke(o0_0,\"value\");"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,\"value\");"
					+ "}",
					"o0_1", true
				},
			new Object[] {
					"map.get(12)",
					"Object o0_0=getVariable(\"map\");"
					+ "Object o0_1=null;"
					+ "try{"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",java.lang.Integer.class).invoke(o0_0,12);"
					+ "}catch(NoSuchMethodException e){"
					+ "o0_1=o0_0.getClass().getMethod(\"get\",Object.class).invoke(o0_0,12);"
					+ "}",
					"o0_1", true
				}
		};
	}
	
}
