package toti.lib.templating.parsing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.templating.Tag;
import toti.lib.templating.TagVariableMode;
import toti.lib.templating.parsing.ParsingInfo;
import toti.lib.templating.parsing.TagParser;
import toti.lib.templating.parsing.enums.TagType;
import toti.lib.templating.parsing.structures.TagParserParam;

public class TagParserTest {
	
	// TODO test with invalid html -> throwing

	@ParameterizedTest
	@MethodSource("dataAcceptWorks")
	public void testAcceptWorks(
			String text, boolean finished, 
			String tagName, boolean isHtml, TagType type,
			List<TagParserParam> parameters) {
		Map<String, Tag> tags = new HashMap<>();
		tags.put(tagName, new Tag() {
			@Override public TagVariableMode getMode(String name) {
				return TagVariableMode.STRING;
			}
			@Override public String getPairStartCode(Map<String, String> params) { return null; }
			@Override public String getPairEndCode(Map<String, String> params) { return null; }
			@Override public String getNotPairCode(Map<String, String> params) { return null; }
			@Override public String getName() { return tagName; }
		});
		TagParser parser = new TagParser(text.charAt(0), tags, new HashMap<>(), new ParsingInfo("", ""));
		assertEquals(finished, ParsingSimulator.simulate(parser, text.substring(1)));
		assertEquals(type, parser.getTagType());
		assertEquals(parameters, parser.getParams());
		assertEquals(tagName, parser.getName());
		assertEquals(isHtml, parser.isHtmlTag());
	}
	
	public static Object[] dataAcceptWorks() {
		return new Object[] {
			new Object[] {
					"p>",
					true,
					"p",
					true,
					TagType.START,
					Arrays.asList()
				},
			new Object[] {
					"p/>",
					true,
					"p",
					true,
					TagType.SINGLE,
					Arrays.asList()
				},
			new Object[] {
					"p />",
					true,
					"p",
					true,
					TagType.SINGLE,
					Arrays.asList()
				},
			new Object[] {
					"/p>",
					true,
					"p",
					true,
					TagType.END,
					Arrays.asList()
				},
			new Object[] {
				"div>",
				true,
				"div",
				true,
				TagType.START,
				Arrays.asList()
			},
			new Object[] {
				"/div>",
				true,
				"div",
				true,
				TagType.END,
				Arrays.asList()
			},
			new Object[] {
				"input />",
				true,
				"input",
				true,
				TagType.SINGLE,
				Arrays.asList()
			},
			new Object[] {
				"input/>",
				true,
				"input",
				true,
				TagType.SINGLE,
				Arrays.asList()
			},
			new Object[] {
					"div class=\"myClass\">",
					true,
					"div",
					true,
					TagType.START,
					Arrays.asList(
						new TagParserParam("class", "myClass", '"')
					)
				},
			new Object[] {
					"div class=\"''\">",
					true,
					"div",
					true,
					TagType.START,
					Arrays.asList(
						new TagParserParam("class", "''", '"')
					)
				},
			new Object[] {
					"div class=\"myClass\" >",
					true,
					"div",
					true,
					TagType.START,
					Arrays.asList(
							new TagParserParam("class", "myClass", '"')
						)
				},
			new Object[] {
					"div class = \"myClass\">",
					true,
					"div",
					true,
					TagType.START,
					Arrays.asList(
							new TagParserParam("class", "myClass", '"')
						)
				},
			new Object[] {
				"div class=\"myClass\" id='ID' required value='' >",
				true,
				"div",
				true,
				TagType.START,
				Arrays.asList(
					new TagParserParam("class", "myClass", '"'),
					new TagParserParam("id", "ID", '\''),
					new TagParserParam("required"),
					new TagParserParam("value", "", '\'')
				)
			},
			new Object[] {
				"t:tag>",
				true,
				"tag",
				false,
				TagType.START,
				Arrays.asList()
			},
			new Object[] {
				"/t:tag>",
				true,
				"tag",
				false,
				TagType.END,
				Arrays.asList()
			},
			new Object[] {
				"t:tag/>",
				true,
				"tag",
				false,
				TagType.SINGLE,
				Arrays.asList()
			},
			new Object[] {
				"t:tag class=\"myClass\" id='ID' required value='' >",
				true,
				"tag",
				false,
				TagType.START,
				Arrays.asList(
						new TagParserParam("class", "myClass", '"'),
						new TagParserParam("id", "ID", '\''),
						new TagParserParam("required"),
						new TagParserParam("value", "", '\'')
					)
			},
			new Object[] {
					"div t:param=\"myClass\">",
					true,
					"div",
					true,
					TagType.START,
					Arrays.asList(
						new TagParserParam("param", "myClass", '"', true)
					)
				},
			new Object[] {
                "div param=\"myClass \n"
                 + " secondClass\"\n"
                 + "param2=\"val\">",
                 true,
                 "div",
                 true,
                 TagType.START,
                 Arrays.asList(
                     new TagParserParam("param", "myClass  secondClass", '"'),
                     new TagParserParam("param2", "val", '"')
                 )
            },
		};
	}
	
}
