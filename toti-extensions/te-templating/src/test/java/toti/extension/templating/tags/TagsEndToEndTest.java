package toti.extension.templating.tags;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.common.structures.MapInit;
import toti.extension.templating.BaseTemplateTestExt;

public class TagsEndToEndTest extends BaseTemplateTestExt {
	
	public TagsEndToEndTest() {
		super("toti/extension/templating/tags");
	}

	@ParameterizedTest
	@MethodSource("dataTags")
	public void testTags(String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			.append("currentMethod", "myMethod")
			.append("securityDoamin", "my-domain")
			.append("toTranslate", "some.key")
			.append("transParam", "some.value")
			.toMap();
		testTemplate(variables, "", template, expected);
	}
	
	public static Object[] dataTags() {
		return new Object[] {
			// if current + else
			new Object[] {
				"ifCurrent.jsp", "If current: "
			},
			new Object[] {
				"ifCurrentElse.jsp", "If current: Not current"
			},
			new Object[] {
				"ifCurrentNot.jsp", "If current: Not current"
			},
			new Object[] {
				"ifCurrentReturning.jsp", "If current: "
			},
			new Object[] {
				"ifCurrentVariable.jsp", "If current: "
			},
			// permissions + else
			new Object[] {
				"permissions.jsp", "Allowed: "
			},
			new Object[] {
				"permissionsElse.jsp", "Allowed: No"
			},
			new Object[] {
				"permissionsNot.jsp", "Allowed: Yes"
			},
			new Object[] {
				"permissionsReturning.jsp", "Allowed: "
			},
			new Object[] {
				"permissionsVariable.jsp", "Allowed: "
			},
			// transate
			new Object[] {
				"translate.jsp", "Trans: some.key {}"
			},
			new Object[] {
				"translateParam.jsp", "Trans: some.key {param=some.value}"
			},
			new Object[] {
				"translateVariable.jsp", "Trans: some.key {}"
			},
			new Object[] {
				"translateReturning.jsp", "Trans: some.key {}"
			},
			new Object[] {
				"translateParamReturning.jsp", "Trans: some.key {param=some.value}"
			},
			new Object[] {
				"translateParamVariable.jsp", "Trans: some.key {param=some.value}"
			},
			//TODO link is missing - no controller
		};
	}
}
