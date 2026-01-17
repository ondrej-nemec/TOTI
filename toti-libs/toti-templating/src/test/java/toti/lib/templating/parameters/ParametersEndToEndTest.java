package toti.lib.templating.parameters;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.templating.BaseTemplateTest;

public class ParametersEndToEndTest extends BaseTemplateTest {
	
	public ParametersEndToEndTest() {
		super("toti/templating/parameters");
	}
	
	@ParameterizedTest
	@MethodSource("dataTags")
	public void testTags(String module, String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			//	.append("totiIdentity", identity)
				.append("toTranslate", "some.key")
				.append("fullLink", "toti.templating.parameters.Controller:index")
				.append("method", "index")
				.toMap();
		testTemplate(variables, module, template, expected);
	}
	
	public static Object[] dataTags() {
		return new Object[] {
			// no tests
		};
	}
}
