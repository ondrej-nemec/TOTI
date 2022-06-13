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
import toti.templating.TagVariableMode;

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
		parser.parse(br, bw, new ParsingInfo("", ""));
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
			// inline
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
			// TODO add translated variables? url variables?
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
            // tag in JS string
            new Object[] {
                true,
                "var tag = '<a href=\"some-url'\r\n + id +'\">'",
                "write(\"var tag = '\");"
                + "write(\"<a href=\\\"some-url' + id +'\\\">\");"
                + "write(\"'\");"
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
			/***** inside *******/			
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
			// variable in returning
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
			// not returning in comment
			new Object[] {
					true,
					"aa <%-- <% some code %> --%> bb",
					"write(\"aa \");"
					+ "write(\" bb\");"
				},
			// not returing in tag
			new Object[] {
					true,
					"class='<% some code %>'",
					"write(\"class='\");"
					+ " some code "
					+ "write(\"'\");"
				},
			// returnign in comment
			new Object[] {
					true,
					"aa <%-- <%= some code %> --%> bb",
					"write(\"aa \");"
					+ "write(\" bb\");"
				},
			// returning in tag
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
			new Object[] {
					true,
					"something <t:tagA class='<%= 10 > 8 ? \"red\" : \"blue\" %>'/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class="
						+" 10 > 8 ? \"red\" : \"blue\" "
						+ "}"
					+ "write(\" text\");"
				},
			// comment in tag
			new Object[] {
					true,
					"something <t:tagA <%-- class=\"clazz\" --%>/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {}"
					+ "write(\" text\");"
				},
			// tag in comment
			new Object[] {
					true,
					"something <%-- <t:tagA class=\"clazz\"/> --%> text",
					"write(\"something \");"
					+ "write(\" text\");"
				},
			
			/* not supported */
			// tag in tag
	        new Object[] {
		            true,
		            "aa <t:tagA class='<t:tagA id=\"my-id\" >' > bb",
		            "write(\"aa \");"
		            + "--tagA-pair-start-- {class=<t:tagA id=\"my-id\" >}"
		            + "write(\" bb\");"
		        },
	        // comment in comment
	        new Object[] {
		            true,
		            "aa <%-- cc <%-- ee --%> dd --%> bb",
		            "write(\"aa \");"
		            + "write(\" dd --%> bb\");" // inside code finish main
		        },
	        // tag with <
			new Object[] {
					true,
					"<t:tag cond='a < b'>",
					"write(\"\");"
					+ "TS a < b"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<div cond='a < b'>",
					"write(\"\");"
					+ "write(\"<div cond='a < b'>\");"
					+ "write(\"\");"
				},
			// tag with $
			new Object[] {
					true,
					"<t:tag cond='a <${var}'>",
					"write(\"\");"
					+ "TS a <getVariable(()->{Object o0_0=getVariable(\"var\");return o0_0;})"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:tag cond='a < ${var}'>",
					"write(\"\");"
					+ "TS a < getVariable(()->{Object o0_0=getVariable(\"var\");return o0_0;})"
					+ "write(\"\");"
				},
			// params and variables
			new Object[] {
					true,
					"<t:testing object=\"aaa\">", //  => String "aaa" 
					"write(\"\");"
					+ "write(aaa);"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing object=\"<%= \"aaa\" %>\">", //  => String "aaa" 
					"write(\"\");"
					+ "write( \"aaa\" );"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing object=\"${var}\">", //  => String "aaa" 
					"write(\"\");"
					+ "write(getVariable(()->{"
						+ "Object o0_0=getVariable(\"var\");return o0_0;})"
					+ ");"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing object=\"aa${var}\">", //  => String "aaa" 
					"write(\"\");"
					+ "write("
						+ "aagetVariable(()->{Object o0_0=getVariable(\"var\");return o0_0;})"
					+ ");"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing object=\"123\">", //  => int 123
					"write(\"\");"
					+ "write(123);"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing object=\"<%= 123 %>\">", //  => int 123
					"write(\"\");"
					+ "write( 123 );"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing code=\"${var|int} > 1\">", //  => getVariable
					"write(\"\");"
					+ "if(new DictionaryValue(getVariable(()->{"
						+ "Object o0_0=getVariable(\"var\");return o0_0;})"
					+ ").getValue(int.class) > 1){write(\"condition\");}"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing code=\"<%= (true ? 2 : 1) %> > 1\">", //  => getVariable
					"write(\"\");"
					+ "if( (true ? 2 : 1)  > 1){write(\"condition\");}"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing string=\"aaa.${var}\">", //  => String "aaa." + getVariable
					"write(\"\");"
					+ "write(\"aaa.\" + (getVariable(()->{Object o0_0=getVariable(\"var\");return o0_0;})) + \"\");"
					+ "write(\"\");"
				},
			new Object[] {
					true,
					"<t:testing string=\"aaa.<%= \"bb\" %>\">", //  => String "aaa." + getVariable
					"write(\"\");"
					+ "write(\"aaa.\" + ( \"bb\" ) + \"\");"
					+ "write(\"\");"
				},
			// returning with < inside
			new Object[] {
					true,
					"something <t:tagA class='<%= List<String> %>'/> text",
					"write(\"something \");"
					+ "--tagA-unpair-- {class="
						+" List<String> "
						+ "}"
					+ "write(\" text\");"
				},
			
			// TODO not-retu in not return
			// TODO not retur in return
			// TODO not-retu in variable
			// TODO return in variable
			// TODO return in not return
			// TODO return in return
			// TODO comment in variable
			// TODO comment in not return
			// TODO comment in return
			// TODO tag in variable
			// TODO tag in not return 
			// TODO tag in return
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
			@Override public TagVariableMode getMode(String name) {
				return TagVariableMode.NOT_SUPPORTED;
			}
		});
		tags.put("tag", new Tag() {
			@Override public TagVariableMode getMode(String name) {
				switch (name) {
					case "cond": return TagVariableMode.CODE;
					//case "string": return TagVariableMode.STRING;
					//case "object": return TagVariableMode.OBJECT;
					default: return TagVariableMode.NOT_SUPPORTED;
				}
			}
			@Override public String getPairStartCode(Map<String, String> params) {
				return "TS " + params.get("cond");
			}
			@Override public String getPairEndCode(Map<String, String> params) {
				return "TE " + params.get("cond");
			}
			@Override public String getNotPairCode(Map<String, String> params) {
				return "T " + params.get("cond");
			}
			@Override public String getName() {
				return "tag";
			}
		});
		tags.put("testing", new Tag() {
			@Override public TagVariableMode getMode(String name) {
				switch (name) {
					case "code": return TagVariableMode.CODE;
					case "string": return TagVariableMode.STRING;
					case "object": return TagVariableMode.OBJECT;
					default: return TagVariableMode.NOT_SUPPORTED;
				}
			}
			@Override public String getPairStartCode(Map<String, String> params) {
				if (params.containsKey("code")) {
					return String.format("if(%s){write(\"condition\");}", params.get("code"));
				}
				if (params.containsKey("string")) {
					return String.format("write(\"%s\");", params.get("string"));
				}
				if (params.containsKey("object")) {
					return String.format("write(%s);", params.get("object"));
				}
				return null;
			}
			@Override public String getPairEndCode(Map<String, String> params) { return null; }
			@Override public String getNotPairCode(Map<String, String> params) { return null; }
			@Override public String getName() { return "testing"; }
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
