package parser.env;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import exceptions.ParserSyntaxException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EnvInputStreamTest {
	
	@Test
	@Parameters
	public void testParseWorks(char[] toParse, boolean[] expectedReturn, String key, String value) {
		EnvInputStream e = new EnvInputStream(null);
		
		if (toParse.length != expectedReturn.length)
			fail("Arrays could not be compare - havenn't got equals length.");
		
		for (int i = 0; i < toParse.length; i++) {
			assertEquals(expectedReturn[i], e.parse(toParse[i]));
		}
		
		assertEquals(value, e.getValue());
		assertEquals(key, e.getKey());
	}
	
	public Collection<List<Object>> parametersForTestParseWorks() {
		return Arrays.asList(
			Arrays.asList(
				new char[] {'a', '=', 'a', '\n'},
				new boolean[] {true, true, true, false},
				"a",
				"a"
			),
			Arrays.asList(
					new char[] {'a', '=', 'a', '\r', '\n'},
					new boolean[] {true, true, true, true, false},
					"a",
					"a"
				),
			Arrays.asList(
					new char[] {'\n'},
					new boolean[] {true},
					"",
					""
				),
			Arrays.asList(
					new char[] {'b', '=', '"', 'a', '"', '\n'},
					new boolean[] {true, true, true, true, true, false},
					"b",
					"a"
				),
			Arrays.asList(
					new char[] {'b', '=', '\'', 'a', '\'', '\n'},
					new boolean[] {true, true, true, true, true, false},
					"b",
					"a"
				),
			Arrays.asList(
					new char[] {'#','t', '\n', },
					new boolean[] {true, true, true, },
					"",
					""
				),
			Arrays.asList(
					new char[] {'e', '=', '\n'},
					new boolean[] {true, true, false},
					"e",
					""
				),
			Arrays.asList(
					new char[] {'b', '=', '"', 'a', '\\', '"', 'c', '"', '\n'},
					new boolean[] {true, true, true, true, true, true, true, true, false},
					"b",
					"a\\\"c"
				),
			Arrays.asList(
					new char[] {'b', '=', '\'', 'a', '\\', '\'', 'c', '\'', '\n'},
					new boolean[] {true, true, true, true, true, true, true, true, false},
					"b",
					"a\\'c"
				),
			Arrays.asList(
					new char[] {'b', '=', '"', 'a', '\\', 'n', 'c', '"', '\n'},
					new boolean[] {true, true, true, true, true, true, true, true, false},
					"b",
					"a\nc"
				),
			Arrays.asList(
					new char[] {'b', '=', ' ', '"', ' ', 'a', ' ', 'a', ' ', '"', ' ', '\n'},
					new boolean[] {true, true, true, true, true, true, true, true, true, true, true, false},
					"b",
					"a a"
				),
			Arrays.asList(
					new char[] {'a', '=', 'a', '#', 'a', '\n'},
					new boolean[] {true, true, true, true, true, false},
					"a",
					"a"
				),
			Arrays.asList(
					new char[] {'b', '=', '\\', '=', '\n'},
					new boolean[] {true, true, true, true, false},
					"b",
					"="
				)
		);
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenKeyDQuotsAreNotClosed() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('k');
		e.parse('=');
		e.parse('"');
		e.parse('v');
		e.parse('\n');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenKeySQuotsAreNotClosed() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('k');
		e.parse('=');
		e.parse('\'');
		e.parse('v');
		e.parse('\n');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenNoEquals() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('k');
		e.parse('\n');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenMoreThanOneEquals() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('k');
		e.parse('=');
		e.parse('=');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenKeysIsEmpty() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('\n');
		e.parse('=');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenDQuotsInKey() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('\n');
		e.parse('k');
		e.parse('"');
	}
	
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenSQuotsInKey() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('\n');
		e.parse('k');
		e.parse('\'');
	}
		
	@Test(expected=ParserSyntaxException.class)
	public void testParseThrowsWhenNotEscapedQuotsInEscapcedString() {
		EnvInputStream e = new EnvInputStream(null);
		
		e.parse('"');
		e.parse('k');
		e.parse('"');
		e.parse('k');
	}
	
	@Test
	public void endToEndTest() {
		try (InputStream is = getClass().getResourceAsStream("/parser/env-input.env")) {
			EnvInputStream parser = new EnvInputStream(is);
			
			int i = 1;
			
			while (parser.next()) {
				switch(i) {
				case 1:
					assertEquals("VARIABLE", parser.getKey());
					assertEquals("text of variable", parser.getValue());
					break;
				case 2:
					assertEquals("NEW_LINE", parser.getKey());
					assertEquals("new\nline", parser.getValue());
					break;
				case 3:
					assertEquals("NEW_LINE_NOT_NOT_ESCAPED", parser.getKey());
					assertEquals("new\\nline", parser.getValue());
					break;
				case 4:
					assertEquals("NEW_LINE_AS_TEXT", parser.getKey());
					assertEquals("new\\nline", parser.getValue());
					break;
				case 5:
					assertEquals("DOUBLE_QUOTES", parser.getKey());
					assertEquals("text", parser.getValue());
					break;
				case 6:
					assertEquals("SINGLE_QUOTES", parser.getKey());
					assertEquals("text", parser.getValue());
					break;
				case 7:
					assertEquals("TRIM", parser.getKey());
					assertEquals("text of trim", parser.getValue());
					break;
				case 8:
					assertEquals("TRIM_WITH_QUOTES", parser.getKey());
					assertEquals("trim", parser.getValue());
					break;
				case 9:
					assertEquals("EMPTY", parser.getKey());
					assertEquals("", parser.getValue());
					break;
				case 10:
					assertEquals("QUOTES_IN_TEXT", parser.getKey());
					assertEquals("text\\\"text\\'text", parser.getValue());
					break;
				case 11:
					assertEquals("var name", parser.getKey());
					assertEquals("name", parser.getValue());
					break;
				
				default:
					throw new RuntimeException("More that " + i + " elements");
				}
				i++;
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
