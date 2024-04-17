package toti.ui.tags;

import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.BaseTemplateTest;
import toti.templating.TemplateContainer;
import toti.ui.TagsProvider;

@RunWith(JUnitParamsRunner.class)
public class TagsEndToEndTest extends BaseTemplateTest {
	
	public TagsEndToEndTest() {
		super(
			"toti/extension/templating/tags",
			new TemplateContainer() {},
			new TagsProvider().getTags(),
			new LinkedList<>()
		);
	}

	@Test
	@Parameters(method="dataTags")
	public void testTags(String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			.toMap();
		testTemplate(variables, "", template, expected);
	}
	
	public Object[] dataTags() {
		return new Object[] {
			//TODO control, grid, form
			/*new Object[] {
				"file.jsp", "result"
			}*/
		};
	}
}
