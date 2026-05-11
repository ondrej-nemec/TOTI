package toti.extension.ui.tags;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.templating.BaseTemplateTest;

public class TagsEndToEndTest extends BaseTemplateTest {
	
	public TagsEndToEndTest() {
		super();
	}

	@ParameterizedTest
	@MethodSource("dataTags")
	public void testTags(String template, String expected) throws Exception {
		Map<String, String> modules = new HashMap<>();
		modules.put("", "toti/extension/templating/tags");
		Map<String, Object> variables = new MapInit<String, Object>()
			.toMap();
		testTemplate(modules, "", template, variables, expected);
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
