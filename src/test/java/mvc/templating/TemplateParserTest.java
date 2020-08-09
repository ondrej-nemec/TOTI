package mvc.templating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.*;
import static common.MapInit.*;

import common.structures.ThrowingConsumer;
import common.structures.ThrowingSupplier;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TemplateParserTest {

	//TODO test missing tag
	
	@Test
	@Parameters(method = "dataLoadFileLoadAndWriteFile")
	public void testLoadFileLoadAndWriteFile(
			ThrowingSupplier<BufferedReader, Exception> br,
			BufferedWriter bw,
			ThrowingConsumer<BufferedWriter, Exception> verify,
			Tag testingTag) {
		Map<String, Tag> tags = new HashMap<>();
		tags.put(testingTag.getName(), testingTag);
		TemplateParser parser = new TemplateParser(tags);
		try {
			parser.loadFile(br.get(), bw);
			verify.accept(bw);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public Object[] dataLoadFileLoadAndWriteFile() {
		return new Object[] {
			new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					when(br.read())
						.thenReturn((int)'t')
						.thenReturn((int)'e')
						.thenReturn((int)'x')
						.thenReturn((int)'t')
						.thenReturn((int)'I')
						.thenReturn((int)'n')
						.thenReturn((int)'L')
						.thenReturn((int)'i')
						.thenReturn((int)'n')
						.thenReturn((int)'e')
						.thenReturn(-1);
					return br;
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(2)).write('t');
					verify(bwMock, times(2)).write('e');
					verify(bwMock, times(1)).write('x');
					//verify(bwMock, times(1)).write('t');
					verify(bwMock, times(1)).write('I');
					verify(bwMock, times(2)).write('n');
					verify(bwMock, times(1)).write('L');
					verify(bwMock, times(1)).write('i');
					//verify(bwMock, times(1)).write('n');
					//verify(bwMock, times(1)).write('e');
				}), mock(Tag.class)
			},
			new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					when(br.read())
						.thenReturn((int)'1')
						.thenReturn((int)'\r')
						.thenReturn((int)'\n')
						.thenReturn((int)'2')
						.thenReturn(-1);
					return br;
					
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(1)).write('1');
					verify(bwMock, times(1)).write("\");b.append(\"\\n");
					verify(bwMock, times(1)).write('2');						
				}), mock(Tag.class)
			},
			new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					when(br.read())
						.thenReturn((int)'1')
						.thenReturn((int)'\"')
						.thenReturn((int)'2')
						.thenReturn(-1);
					return br;
					
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(1)).write('1');
					verify(bwMock, times(1)).write('\\');
					verify(bwMock, times(1)).write('\"');
					verify(bwMock, times(1)).write('2');						
				}), mock(Tag.class)
			},
			new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					when(br.read())
						.thenReturn((int)'1')
						.thenReturn((int)'\\')
						.thenReturn((int)'2')
						.thenReturn(-1);
					return br;
					
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(1)).write('1');
					verify(bwMock, times(2)).write('\\');
					verify(bwMock, times(1)).write('2');						
				}), mock(Tag.class)
			},
			new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					/* <html class="text" id='text'></html> */
					setBufferedReader(br, "<html class=\"text\" id='text'></html>");
					return br;
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(1)).write("<");
					verify(bwMock, times(1)).write("</");
					
					verify(bwMock, times(2)).write('h');
					verify(bwMock, times(6)).write('t');
					verify(bwMock, times(2)).write('m');
					verify(bwMock, times(3)).write('l');
					verify(bwMock, times(2)).write(' ');
					verify(bwMock, times(1)).write('c');
					verify(bwMock, times(1)).write('a');
					verify(bwMock, times(2)).write('s');
					verify(bwMock, times(2)).write('=');
					verify(bwMock, times(2)).write('\"');
					verify(bwMock, times(2)).write('e');
					verify(bwMock, times(2)).write('x');
					verify(bwMock, times(1)).write('i');
					verify(bwMock, times(1)).write('d');
					verify(bwMock, times(2)).write('\'');
					verify(bwMock, times(2)).write('>');
				}), mock(Tag.class)
			},
			new Object[] {
					initBr(()->{
						BufferedReader br = mock(BufferedReader.class);
						when(br.read())
							.thenReturn((int)'<')
							.thenReturn((int)'t')
							.thenReturn((int)'a')
							.thenReturn((int)'b')
							.thenReturn((int)'l')
							.thenReturn((int)'e')
							.thenReturn((int)'>')
							.thenReturn((int)'<')
							.thenReturn((int)'/')
							.thenReturn((int)'t')
							.thenReturn((int)'a')
							.thenReturn((int)'b')
							.thenReturn((int)'l')
							.thenReturn((int)'e')
							.thenReturn((int)'>')
							.thenReturn(-1);
						return br;
						
					}), mock(BufferedWriter.class), initBw((bwMock)->{
						verify(bwMock, times(1)).write("<t");
						verify(bwMock, times(1)).write("</t");
						
						verify(bwMock, times(2)).write('a');
						verify(bwMock, times(2)).write('b');
						verify(bwMock, times(2)).write('l');
						verify(bwMock, times(2)).write('e');
						verify(bwMock, times(2)).write('>');						
					}), mock(Tag.class)
				},
			// <t:testingTag >
			getTagTest("<t:testingTag >", 1, new HashMap<>()),
			// <t:testingTag>
			getTagTest("<t:testingTag>", 1, new HashMap<>()),
			// </t:testingTag>
			getTagTest("</t:testingTag>", 1, new HashMap<>()),
			// </t:testingTag >
			getTagTest("</t:testingTag >", 1, new HashMap<>()),
			// <t:testingTag />
			getTagTest("<t:testingTag />", 1, new HashMap<>()),
			// <t:testingTag class="body1" id="body2">
			getTagTest("<t:testingTag class=\"body1\" id=\"body2\">", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag class="body1" id='body2'/>
			getTagTest("<t:testingTag class=\"body1\" id='body2'/>", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag class="body1" id='body2' >
			getTagTest("<t:testingTag class=\"body1\" id='body2' >", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag class="body1" id='body2' />
			getTagTest("<t:testingTag class=\"body1\" id='body2' />", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag id="<> \ \" ' ">
			getTagTest("<t:testingTag id=\"<> \\ \\\" ' \">", 1, hashMap(t("id", "<> \\ \\\" '"))),
			// <t:testingTag id='<> \ " \' '>
			getTagTest( "<t:testingTag id='<> \\ \" \\' '>", 1, hashMap(t("id", "<> \" \\' "))),
			// <t:testingTag class="body1"id='body2' >
			getTagTest("<t:testingTag class=\"body1\"id='body2' >", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag  class="body1"  id='body2' >
			getTagTest("<t:testingTag  class=\"body1\"  id='body2' >", 1, hashMap(t("class", "body1"), t("id", "body2"))),
			// <t:testingTag  class id='body2' >
			getTagTest("<t:testingTag  class id='body2' >", 1, hashMap(t("class", ""), t("id", "body2"))),
			
		};
	}
	
	private Object[] getTagTest(String text, int callCount, Map<String, String> params) {
		Tag tag = mock(Tag.class);
		when(tag.getName()).thenReturn("testingTag");
		when(tag.getCode(any())).thenReturn("body");
		return new Object[] {
				initBr(()->{
					BufferedReader br = mock(BufferedReader.class);
					setBufferedReader(br, text);
					return br;
				}), mock(BufferedWriter.class), initBw((bwMock)->{
					verify(bwMock, times(1)).write("body");
					verify(tag, times(callCount)).getCode(params);
				}), tag
			};
	}
	
	public ThrowingSupplier<BufferedReader, Exception> initBr(ThrowingSupplier<BufferedReader, Exception> init) {
		return init;
	}
	
	public ThrowingConsumer<BufferedWriter, Exception> initBw(ThrowingConsumer<BufferedWriter, Exception> verify) {
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
