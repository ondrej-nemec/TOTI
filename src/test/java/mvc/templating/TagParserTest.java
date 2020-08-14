package mvc.templating;

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

@RunWith(JUnitParamsRunner.class)
public class TagParserTest {

	//TODO test missing tag
	
	
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
		
		TagParser parser = new TagParser(tags, isClosingTag);
		
		char previous = '\u0000';
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		for (char c : template.toCharArray()) {
			if (c == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
			} else if (c == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
			}
			parser.parse(c, isSingleQuoted, isDoubleQuoted, previous);
			previous = c;
		}
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
