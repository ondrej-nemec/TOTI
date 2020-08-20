package mvc.templating.parsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static common.MapInit.*;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import mvc.templating.Tag;
import mvc.templating.parsing.TagParser;

@RunWith(JUnitParamsRunner.class)
public class TagParserTest {

	//TODO check  wrong cases
	@Test
	@Parameters(method = "dataParseThrowsOnWrongText")
	public void testParseThrowsOnWrongText(String template, boolean isClosingTag) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Tag> tags = mock(Map.class);		
		
		TagParser parser = new TagParser(tags, isClosingTag);
		
		char previous = '\u0000';
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		for (char c : template.toCharArray()) {
			boolean write = true;
			if (c == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
				write = false;
			} else if (c == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
				write = false;
			}
			if (write) {
				parser.parse(c, isSingleQuoted, isDoubleQuoted, previous);
			}
			previous = c;
		}
		verifyNoMoreInteractions(tags);
	}
	
	public Object[] dataParseThrowsOnWrongText() {
		return new Object[] {
			new Object[] {
				"testng\"", false
			},
			new Object[] {
				"testng \"", false
			},
			new Object[] {
					"testng class=\"value>", false
				},
			new Object[] {
					"testng class=value>", false
				},
			new Object[] {
					"testng 'class'=value>", false
				},
			new Object[] {
					"testng <", false
				},
		};
	}
	
	@Test
	@Parameters(method = "dataParseWorks")
	public void testParseWorks(
			String template,
			String expectedHtml,
			Consumer<Tag> verifyTag, boolean isClosingTag) throws IOException {
		Tag tag = mock(Tag.class);
		when(tag.getName()).thenReturn("testingTag");
		when(tag.getPairStartCode(any())).thenReturn("starting-tag");
		when(tag.getPairEndCode(any())).thenReturn("ending-tag");
		when(tag.getNotPairCode(any())).thenReturn("non-pair-tag");
		Map<String, Tag> tags = new HashMap<>();
		tags.put("testingTag", tag);
		
		TagParser parser = new TagParser(tags, isClosingTag);
		
		char previous = '\u0000';
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		for (char c : template.toCharArray()) {
			boolean write = true;
			if (c == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
				write = false;
			} else if (c == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
				write = false;
			}
			if (write) {
				parser.parse(c, isSingleQuoted, isDoubleQuoted, previous);
			}
			previous = c;
		}
		verifyTag.accept(tag);
	}
	
	public Object[] dataParseWorks() {
		return new Object[] {
			new Object[] {
					"testingTag >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{}), false
				},
			new Object[] {
					"testingTag>",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{}), false
				},
			new Object[] {
					"testingTag>",
					"\");ending-tagb.append(\"",
					getVerify((tag)->{}), true
				},
			new Object[] {
					"testingTag >",
					"\");ending-tagb.append(\"",
					getVerify((tag)->{}), true
				},
			new Object[] {
					"testingTag />",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{}), false
				},
			new Object[] {
					"testingTag class=\"body1\" id=\"body2\">",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
					}), false
				},
			new Object[] {
					"testingTag class=\"body1\" id='body2'/>",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getNotPairCode(hashMap(t("class", "body1"), t("id", "body2")));
						}), false
				},
			new Object[] {
					"testingTag class=\"body1\" id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						}), false
				},
			new Object[] {
					"testingTag class=\"body1\" id='body2' />",
					"\");non-pair-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getNotPairCode(hashMap(t("class", "body1"), t("id", "body2")));
						}), false
				},
			new Object[] {
					"testingTag id=\"<> \\ \\\" ' \">",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "<> \\ \\\" ' ")));
						}), false
				},
			new Object[] {
					"testingTag id='<> \\ \" \\' '>",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("id", "<> \\ \" \\' ")));
						}), false
				},
			new Object[] {
					"testingTag class=\"body1\"id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						}), false
				},
			new Object[] {
					"testingTag  class=\"body1\"  id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", "body1"), t("id", "body2")));
						}), false
				},
			new Object[] {
					"testingTag  class id='body2' >",
					"\");starting-tagb.append(\"",
					getVerify((tag)->{
						verify(tag, times(1)).getPairStartCode(hashMap(t("class", ""), t("id", "body2")));
						}), false
				},
		};
	}
	
	private Consumer<Tag> getVerify(Consumer<Tag> verify) {
		return verify;
	}
	
}
