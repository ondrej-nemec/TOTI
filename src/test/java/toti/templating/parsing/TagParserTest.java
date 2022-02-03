package toti.templating.parsing;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.parsing.enums.TagType;
import toti.templating.parsing.structures.TagParserParam;

@RunWith(JUnitParamsRunner.class)
public class TagParserTest {
	
	// TODO test with invalid html -> throwing
	
	@Test
	@Ignore
	public void testAddVariable() {
		TagParser parser = new TagParser('d');
		VariableParser var = new VariableParser(0);
		
		ParsingSimulator.simulate(parser, "iv class='-");
		ParsingSimulator.simulate(var, "title}");
		parser.addVariable(var);
		ParsingSimulator.simulate(parser, "-'>");
		
		assertEquals(
			"",
			parser.getAsString()
		);
		
		/*
		"getVariable(()->{"
				+ "Object o0_0=getVariable(\"title\");"
				+ "return o0_0;"
				+ "})",
		 */
	}

	@Test
	@Parameters(method="dataAcceptWorks")
	public void testAcceptWorks(
			String text, boolean finished, 
			String tagName, boolean isHtml, TagType type,
			List<TagParserParam> parameters) {
		TagParser parser = new TagParser(text.charAt(0));
		assertEquals(finished, ParsingSimulator.simulate(parser, text.substring(1)));
		assertEquals(type, parser.getTagType());
		assertEquals(parameters, parser.getParams());
		assertEquals(tagName, parser.getName());
		assertEquals(isHtml, parser.isHtmlTag());
	}
	
	public Object[] dataAcceptWorks() {
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
		};
	}
	
}


