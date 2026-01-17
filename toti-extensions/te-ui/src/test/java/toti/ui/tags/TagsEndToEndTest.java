package toti.ui.tags;

import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.templating.BaseTemplateTest;
import toti.lib.templating.TemplateContainer;
import toti.ui.TagsProvider;

public class TagsEndToEndTest extends BaseTemplateTest {
	
	public TagsEndToEndTest() {
		super(
			"toti/extension/templating/tags",
			new TemplateContainer() {},
			new TagsProvider().getTags(),
			new LinkedList<>()
		);
	}

	@ParameterizedTest
	@MethodSource("dataTags")
	public void testTags(String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			.toMap();
		testTemplate(variables, "", template, expected);
	}
	
	public static Object[] dataTags() {
		return new Object[] {
			//TODO control, grid, form
			/*new Object[] {
				"file.jsp", "result"
			}*/
		};
	}
}
