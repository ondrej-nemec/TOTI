package toti.templating.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;

import common.structures.MapInit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.Tag;
import toti.templating.parsing.TemplateParser;

@RunWith(JUnitParamsRunner.class)
public class TemplateParserTest {

	//TODO test missing tag
	
	
	@Test
	@Parameters(method = "dataLoadFileLoadAndWriteFile")
	public void testLoadFileLoadAndWriteFile(
			String template,
			String expectedHtml,
			Consumer<Tag> verifyTag) throws IOException {
		Tag tag = mock(Tag.class);
		when(tag.getName()).thenReturn("testingTag");
		when(tag.getPairStartCode(any())).thenReturn("/* starting-tag */");
		when(tag.getPairEndCode(any())).thenReturn("/* ending-tag */");
		when(tag.getNotPairCode(any())).thenReturn("/* non-pair-tag */");
		Map<String, Tag> tags = new HashMap<>();
		tags.put(tag.getName(), tag);
		
		StringBuilder bw = new StringBuilder();
		
		BufferedReader br = mock(BufferedReader.class);
		setBufferedReader(br, template);
		
		TemplateParser parser = new TemplateParser(tags, false);
		parser.loadFile(br, (text)->{
			bw.append(text);
		});
		
		assertEquals(expectedHtml, bw.toString());
		verifyTag.accept(tag);
	}
	
	public Object[] dataLoadFileLoadAndWriteFile() {
		return new Object[] {
			// test is closing tag
			new Object[] {
					"<t:testingTag>",
					"\");"
					+ "/* starting-tag */"
					+ "b.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
					"</t:testingTag>",
					"\");"
					+ "/* ending-tag */"
					+ "b.append(\"",
					getVerify((tag)->{})
				},
			// specials called in html comment
			new Object[] {
					"1<!-- <t:testingTag /> -->2",
					"1<!-- \");"
					+ "/* non-pair-tag */"
					+ "b.append(\" -->2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1<!-- ${var} -->2",
					"1<!-- \");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "b.append(Template.escapeVariable(o0_1));"
					+ "b.append(\" -->2",
					getVerify((tag)->{})
				},
			// tag and var integartion
			new Object[] {
					"<t:testingTag  class id='${var}' >",
					"\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "/* starting-tag */b.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(
							new MapInit<String, String>().append("id", "o0_1").append("class", "").toMap()
						);
					})
				},
			new Object[] {
					"1${var}2",
					"1\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "b.append(Template.escapeVariable(o0_1));"
					+ "b.append(\"2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1${var.equals(${var2})}2",
					"1\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "Object o1_1=variables.get(\"var2\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"equals\",java.lang.String.class).invoke(o0_1,o1_1);"
					+ "b.append(Template.escapeVariable(o0_2));"
					+ "b.append(\"2",
					getVerify((tag)->{})
				},
			/*new Object[] {
				"textInLine",
				"textInLine",
				getVerify((tag)->{})
			},
			new Object[] {
					"1\r\n2",
					"1\");b.append(\"\\n2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1\"2",
					"1\\\"2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1\\2",
					"1\\\\2",
					getVerify((tag)->{})
				},
			new Object[] {
					"<html class=\"text\" id='text'></html>",
					"<html class=\\\"text\\\" id='text'></html>",
					getVerify((tag)->{})
				},
			new Object[] {
					"<table></table>",
					"<table></table>",
					getVerify((tag)->{})
				},*/
		/*	new Object[] {
					"<t:testingTag >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{})
				},*/
		/*	new Object[] {
					"</t:testingTag >",
					"\");ending-tagb.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
					"<t:testingTag />",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
					"<t:testingTag class=\"body1\" id=\"body2\">",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
					})
				},
			new Object[] {
					"<t:testingTag class=\"body1\" id='body2'/>",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getNotPairCode(hashMap(t("class", "body1"), t("id", "body2")));
						})
				},
			new Object[] {
					"<t:testingTag  ${var} id='id' >",
					"\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ " starting-tag "
					+ "b.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "id"), t("class", "")));
					})
				},
			new Object[] {
					"<t:testingTag class=\"body1\" id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						})
				},
			new Object[] {
					"<t:testingTag class=\"body1\" id='body2' />",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getNotPairCode(hashMap(t("class", "body1"), t("id", "body2")));
						})
				},
			new Object[] {
					"<t:testingTag id=\"<> \\ \\\" ' \">",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "<> \\ \\\" ' ")));
						})
				},
			new Object[] {
					"<t:testingTag id='<> \\ \" \\' '>",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "<> \\ \" \\' ")));
						})
				},
			new Object[] {
					"<t:testingTag class=\"body1\"id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						})
				},
			new Object[] {
					"<t:testingTag  class=\"body1\"  id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						})
				},
			new Object[] {
					"<t:testingTag  class id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", ""), t("id", "body2")));
						})
				},*/
		/*	new Object[] {
					"1<%-- <t:testingTag /> --%>2",
					"12",
					getVerify((tag)->{})
				},
			new Object[] {
					"1<%-- ${var} --%>2",
					"12",
					getVerify((tag)->{})
				},*/
			/*new Object[] {
					"1$2",
					"1$2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1${var.equals(1)}2",
					"1\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"equals\").invoke(o0_1, 1);"
					+ "b.append(Template.escapeVariable(o0_2));"
					+ "b.append(\"2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1${var.getClass().equals(1)}2",
					"1\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getClass\").invoke(o0_1);"
					+ "Object o0_3=o0_2.getClass().getMethod(\"equals\").invoke(o0_2, 1);"
					+ "b.append(Template.escaoeVariable(o0_3));"
					+ "b.append(\"2",
					getVerify((tag)->{})
				},
			new Object[] {
					"1${var.class.equals(1)}2",
					"1\");"
					+ "Object o0_1=variables.get(\"var\");"
					+ "Object o0_2=o0_1.getClass().getMethod(\"getClass\").invoke(o0_1);"
					+ "Object o0_3=o0_2.getClass().getMethod(\"equals\").invoke(o0_2, 1);"
					+ "b.append(Template.escapeVariable(o0_3));"
					+ "b.append(\"2",
					getVerify((tag)->{})
				},*/
		};
	}
	
	@Test
	@Parameters(method = "dataWriteHtmlWorks")
	public void testWriteHtmlWorks(
			String template,
			String expectedHtml) throws IOException {
		Tag tag = mock(Tag.class);
		Map<String, Tag> tags = new HashMap<>();
		tags.put("testingTag", tag);
		
		StringBuilder bw = new StringBuilder();
		
		BufferedReader br = mock(BufferedReader.class);
		setBufferedReader(br, template);
		
		TemplateParser parser = new TemplateParser(tags, false);
		parser.loadFile(br, (text)->{
			bw.append(text);
		});
		
		assertEquals(expectedHtml, bw.toString());
		verifyNoMoreInteractions(tag);
	}
	
	public Object[] dataWriteHtmlWorks() {
		return new Object[] {
			new Object[] {
				"textInLine",
				"textInLine"
			},
			new Object[] {
					"1\r\n2",
					"1\");b.append(\"\\n2"
				},
			new Object[] {
					"1\"2",
					"1\\\"2"
				},
			new Object[] {
					"1\\2",
					"1\\\\2"
				},
			new Object[] {
					"<html class=\"text\" id='text'></html>",
					"<html class=\\\"text\\\" id='text'></html>"
				},
			new Object[] {
					"<table></table>",
					"<table></table>"
				},
			new Object[] {
					"1$2",
					"1$2",
				},
			new Object[] {
					"<%aa",
					"<%aa",
				},
			new Object[] {
					"<\"",
					"<\\\"",
				},
			new Object[] {
					"<<\"",
					"<<\\\"",
				},
			new Object[] {
					"$(js here)",
					"$(js here)",
				},
		};
	}
	@Test
	@Parameters(method = "dataCommentsWorks")
	public void testCommentsWorks(
			String template,
			String expectedHtml) throws IOException {
		Tag tag = mock(Tag.class);
		Map<String, Tag> tags = new HashMap<>();
		tags.put("testingTag", tag);
		
		StringBuilder bw = new StringBuilder();
		
		BufferedReader br = mock(BufferedReader.class);
		setBufferedReader(br, template);
		
		TemplateParser parser = new TemplateParser(tags, false);
		parser.loadFile(br, (text)->{
			bw.append(text);
		});
		
		assertEquals(expectedHtml, bw.toString());
		verifyNoMoreInteractions(tag);
	}
	
	public Object[] dataCommentsWorks() {
		return new Object[] {
			new Object[] {
					"1<%-- <t:testingTag /> --%>2",
					"12",
				},
			new Object[] {
					"1<%-- ${var} --%>2",
					"12",
				},
			new Object[] {
					"1<%-- <li class=\"nav-item\">Text</li> --%>2",
					"12",
				},
			new Object[] {
					"1<%-- <li class=\"nav-item\">\r\n" + 
					"		    <a class=\"nav-link active\" aria-current=\"page\" href=\"#\">Active</a>\r\n" + 
					"		  </li> --%>2",
					"12",
				},
			
			
			
		};
	}
	
	private Consumer<Tag> getVerify(Consumer<Tag> verify) {
		return verify;
	}
	
	private void setBufferedReader(BufferedReader br, String text) throws IOException {
		 setBufferedReader(when(br.read()), text.toCharArray(), 0);
	}
	
	private void setBufferedReader(OngoingStubbing<Integer> mock, char[] chars, int index) {
		if (chars.length <= index) {
			mock.thenReturn(-1);
			return;
		}
		OngoingStubbing<Integer> newMock = mock.thenReturn((int)chars[index]);
		setBufferedReader(newMock, chars, index+1);
	}
	
}
