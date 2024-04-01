package toti.templating.parameters;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.templating.BaseTemplateTest;

@RunWith(JUnitParamsRunner.class)
public class ParametersEndToEndTest extends BaseTemplateTest {
	
	public ParametersEndToEndTest() {
		super("toti/templating/parameters");
	}
	
	@Test
	@Parameters(method="dataTags")
	public void testTags(String module, String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			//	.append("totiIdentity", identity)
				.append("toTranslate", "some.key")
				.append("fullLink", "toti.templating.parameters.Controller:index")
				.append("method", "index")
				.toMap();
		testTemplate(variables, module, template, expected);
	}
	
	public Object[] dataTags() {
		return new Object[] {
			// no tests
		};
	}
}
