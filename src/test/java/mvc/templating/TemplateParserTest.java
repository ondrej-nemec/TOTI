package mvc.templating;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static common.MapInit.*;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TemplateParserTest {

	//TODO test missing tag
	
	
	@Test
	@Parameters(method = "dataLoadFileLoadAndWriteFile")
	public void testLoadFileLoadAndWriteFile(
			String template,
			String expectedHtml,
			Consumer<Tag> verifyTag) {
		Tag tag = mock(Tag.class);
		when(tag.getName()).thenReturn("testingTag");
		when(tag.getPairStartCode(any())).thenReturn("starting-tag");
		when(tag.getPairEndCode(any())).thenReturn("ending-tag");
		when(tag.getNotPairCode(any())).thenReturn("non-pair-tag");
		Map<String, Tag> tags = new HashMap<>();
		tags.put(tag.getName(), tag);
		
		StringBuilder bw = new StringBuilder();
		
		try {
			BufferedReader br = mock(BufferedReader.class);
			setBufferedReader(br, template);
		
			TemplateParser parser = new TemplateParser(tags);
			parser.loadFile(br, (text)->{
				bw.append(text);
			});
			
			assertEquals(expectedHtml, bw.toString());
			verifyTag.accept(tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object[] dataLoadFileLoadAndWriteFile() {
		return new Object[] {
			new Object[] {
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
				},
			new Object[] {
					"<t:testingTag >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
					"<t:testingTag>",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
					"</t:testingTag>",
					"\");ending-tagb.append(\"",
					getVerify((tag)->{})
				},
			new Object[] {
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
				},
			new Object[] {
					"1$2",
					"1$2",
					getVerify((tag)->{})
				},
			new Object[] {
					"<t:testingTag  class id='${var}' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "body2")));
					})
				},
			new Object[] {
					"1${var}2",
					"1\");b.append(escapreVariable(variables.get(\"var\").toString()));b.append(\"2",
					getVerify((tag)->{})
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
