package toti.templating.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.ThrowingConsumer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.Parameter;
import toti.templating.Tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)	
public class TemplateParserTest {
	
	// TODO throwing tests
	// TODO test with variable escape
	
	@Test
	@Parameters(method="dataParseWorks")
	public void testParseWorks(boolean minimalize, String text, String expected) throws IOException {
		TemplateParser parser = new TemplateParser(getTestingTags(), getTestingParameters(), minimalize);
		BufferedReader br = getReader(text);
		ThrowingConsumer<String, IOException> bw = (actual)->{
			assertEquals(expected, actual);
		};
		parser.parse(br, bw);
	}

	
	public Object[] dataParseWorks() {
		return new Object[] {
			new Object[] {
				true,
				"some text",
				"write(\"some text\");"
			},
			new Object[] {
					true,
					"some < text",
					"write(\"some < text\");"
				},
			new Object[] {
					true,
					"some  text",
					"write(\"some text\");"
				},
			new Object[] {
					true,
					"some\t text",
					"write(\"some text\");"
				},
			new Object[] {
					false,
					"some\t text",
					"write(\"some\t text\");"
				},
			new Object[] {
					true,
					"some text\n on another line",
					"write(\"some text\");write(\" on another line\");"
				},
			new Object[] {
					true,
					"some text\r\n on another line",
					"write(\"some text\");write(\" on another line\");"
				},
			new Object[] {
					false,
					"some text\n on another line",
					"write(\"some text\\n\");write(\" on another line\");"
				},
			// java code
			new Object[] {
				true,
				"some text <% java call %> text continue",
				"write(\"some text \"); java call write(\" text continue\");"
			},
			// comment
			new Object[] {
				true,
				"some text <%-- comment --%> text continue",
				"write(\"some text \");write(\" text continue\");"
			},
			// TODO this is deprecated
			// inline
			new Object[] {
				true,
				"the value is {{ inline java code }}!",
				"write(\"the value is \");write( inline java code );write(\"!\");"
			},
			new Object[] {
				true,
				"the value is <%= inline java code %>!",
				"write(\"the value is \");write( inline java code );write(\"!\");"
			},
			// variable
			new Object[] {
					true,
					"another value: ${value}.",
					"write(\"another value: \");"
					+ "write(Template.escapeHtml("
					+ "getVariable(()->{"
					+ "Object o0_0=getVariable(\"value\");"
					+ "return o0_0;"
					+ "})"
					+ "));"
					+ "write(\".\");"
				},
			new Object[] {
					true,
					"another value: ${value|noescape}.",
					"write(\"another value: \");write("
					+ "getVariable(()->{"
					+ "Object o0_0=getVariable(\"value\");"
					+ "return o0_0;"
					+ "})"
					+ ");"
					+ "write(\".\");"
				},
			// TODO add translated variables?
			// html tag
			new Object[] {
					true,
					"<div class=\"greeting\">Hello World!</div>",
					"write(\"\");"
					+ "write(\"<div class=\\\"greeting\\\">\");"
					+ "write(\"Hello World!\");"
					+ "write(\"</div>\");"
					+ "write(\"\");"
				},
			// tag
			new Object[] {
					true,
					"something <t:tagA class=\"clazz\">content</t:tagA> text",
					"write(\"something \");"
					+ "--tagA-pair-start-- {class=clazz}"
					+ "write(\"content\");"
					+ "--tagA-pair-end-- {}"
					+ "write(\" text\");"
				},
			new Object[] {
					true,
					"something <t:tagA class=\"clazz\"/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class=clazz}"
					+ "write(\" text\");"
				},
			// variable in variable
			new Object[] {
					true,
					"texts are ${first.equals(${second})}",
					"write(\"texts are \");write(Template.escapeHtml("
						+"getVariable(()->{"
						+ "Object o0_0=getVariable(\"first\");"
						+ "Object o1_0_aux=getVariable(()->{"
							+ "Object o1_0=getVariable(\"second\");"
							+ "return o1_0;"
						+ "});"
						+ "Object o0_1=null;"
						+ "try{"
						+ "o0_1=o0_0.getClass().getMethod(\"equals\",o1_0_aux.getClass()).invoke(o0_0,o1_0_aux);"
						+ "}catch(NoSuchMethodException e){"
						+ "o0_1=o0_0.getClass().getMethod(\"equals\",Object.class).invoke(o0_0,o1_0_aux);"
						+ "}"
						+ "return o0_1;"
						+ "})"
					+ "));"
					+ "write(\"\");"
				},
			// variable in inline
			// TODO this is deprecated
			new Object[] {
					true,
					"class='{{ ${color} > 8 ? \"red\" : \"blue\" }}'",
					"write(\"class='\");"
					+ "write( "
						+"Template.escapeHtml(getVariable(()->{"
						+ "Object o0_0=getVariable(\"color\");"
						+ "return o0_0;"
						+ "}))"
					  + " > 8 ? \"red\" : \"blue\" );"
					+ "write(\"'\");"
				},
			new Object[] {
				true,
				"class='<%= ${color} > 8 ? \"red\" : \"blue\" %>'",
				"write(\"class='\");"
				+ "write( "
					+"getVariable(()->{"
					+ "Object o0_0=getVariable(\"color\");"
					+ "return o0_0;"
					+ "})"
				  + " > 8 ? \"red\" : \"blue\" );"
				+ "write(\"'\");"
			},
			// comment in tag
			new Object[] {
					true,
					"something <t:tagA <%-- class=\"clazz\" --%>/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {}"
					+ "write(\" text\");"
				},
			// variable in tag
			new Object[] {
					true,
					"something <t:tagA class='${clazz}'/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class="
						+"getVariable(()->{"
						+ "Object o0_0=getVariable(\"clazz\");"
						+ "return o0_0;"
						+ "})"
						+ "}"
					+ "write(\" text\");"
				},
			new Object[] {
					true,
					"something <t:tagA class=\"${clazz}\"/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class="
						+"getVariable(()->{"
						+ "Object o0_0=getVariable(\"clazz\");"
						+ "return o0_0;"
						+ "})"
						+ "}"
					+ "write(\" text\");"
				},
			new Object[] {
					true,
					"something <div class=\"${clazz}\"/> text",
					"write(\"something \");write(\"<div class=\\\"\" + "
						+"Template.escapeHtml(getVariable(()->{"
						+ "Object o0_0=getVariable(\"clazz\");"
						+ "return o0_0;"
						+ "}))"
					+ " + \"\\\" />\");write(\" text\");"
				},
			// inline in tag // TODO deprecated
			new Object[] {
					true,
					"something <t:tagA class='{{ 9 > 4 ? 'D' : 'L' }}'/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class=( 9 > 4 ? 'D' : 'L' )}"
					+ "write(\" text\");"
				},
			new Object[] {
					true,
					"something <div class='{{ 9 > 4 ? 'D' : 'L' }}'/> text",
					"write(\"something \");"
					+ "write(\"<div class='\" + ( 9 > 4 ? 'D' : 'L' ) + \"' />\");"
					+ "write(\" text\");"
				},
			// tag with parameter
			new Object[] {
					true,
					"text <div t:paramA=\"value\">",
					"write(\"text \");"
					+ "write(\"<div paramA=\\\"\"+"
					+ "-- parameter - value --"
					+ "+\"\\\">\");"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"something <t:tagA t:paramA=\"clazz\"/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {paramA=-- parameter - clazz --}"
					+ "write(\" text\");"
				},
			// variable in code
			new Object[] {
					true,
					"something <% System.out.println(${clazz}); %> text",
					"write(\"something \");"
						+" System.out.println(getVariable(()->{"
						+ "Object o0_0=getVariable(\"clazz\");"
						+ "return o0_0;"
						+ "})); "
					+ "write(\" text\");"
				},
			new Object[] {
					true,
					"something <%-- System.out.println(${clazz}); --%> text",
					"write(\"something \");write(\" text\");"
				},
			new Object[] {
					true,
					"something <%= ${clazz} %> text",
					"write(\"something \");"
						+"write( getVariable(()->{"
						+ "Object o0_0=getVariable(\"clazz\");"
						+ "return o0_0;"
						+ "}) );"
					+ "write(\" text\");"
				},
		};
	}
	
	private Map<String, Parameter> getTestingParameters() {
		Map<String, Parameter> parameters = new HashMap<>();
		parameters.put("paramA", new Parameter() {
			@Override public String getName() {
				return "paramA";
			}
			@Override public String getCode(String value) {
				return String.format("-- parameter - %s --", value);
			}
		});
		return parameters;
	}

	private Map<String, Tag> getTestingTags() {
		Map<String, Tag> tags = new HashMap<>();
		tags.put("tagA", new Tag() {
			@Override public String getPairStartCode(Map<String, String> params) {
				return "--tagA-pair-start-- " + params;
			}
			@Override public String getPairEndCode(Map<String, String> params) {
				return "--tagA-pair-end-- " + params;
			}
			@Override public String getNotPairCode(Map<String, String> params) {
				return "--tagA-unpair-- " + params;
			}
			@Override public String getName() {
				return "tagA";
			}
		});
		return tags;
	}


	private BufferedReader getReader(String text) throws IOException {
		BufferedReader br = mock(BufferedReader.class);
		Iterator<Integer> values = text.chars().iterator();
		when(br.read()).thenAnswer(i ->{
			if (values.hasNext()) {
				return values.next();
			}
			return -1;
		});
		return br;
	}
	
}
