package toti.lib.templating.parameters;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.templating.BaseTemplateTest;

public class ParametersEndToEndTest extends BaseTemplateTest {
	
	public ParametersEndToEndTest() {
		super();
	}
	
	@ParameterizedTest
	@MethodSource("dataTags")
	@Disabled
	public void testTags(String module, String template, String expected) throws Exception {
		Map<String, String> modules = new HashMap<>();
		modules.put(module, "toti/templating/parameters/" + module);
		Map<String, Object> variables = new MapInit<String, Object>().toMap();
		testTemplate(modules, module, template, variables, expected);
	}
	
	public static Object[] dataTags() {
		return new Object[] {
			// no tests
		};
	}
}
