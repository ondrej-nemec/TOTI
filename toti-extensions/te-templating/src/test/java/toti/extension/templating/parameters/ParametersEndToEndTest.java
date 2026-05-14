package toti.extension.templating.parameters;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.templating.BaseTemplateTestExt;
import toti.lib.common.structures.MapInit;

public class ParametersEndToEndTest extends BaseTemplateTestExt {

	public ParametersEndToEndTest() {
		super();
	}
	
	@ParameterizedTest
	@MethodSource("data")
	public void test(String template, String expected) throws Exception {
		Map<String, String> modules = new HashMap<>();
		modules.put("", "toti/extension/templating/parameters");
		Map<String, Object> variables = new MapInit<String, Object>()
			//	.append("totiIdentity", identity)
				.append("toTranslate", "some.key")
				.append("fullLink", "toti.templating.parameters.Controller:index")
				.append("method", "index")
				.toMap();
		testTemplate(modules, "", template, variables, expected);
	}
	
	public static Object[] data() {
		return new Object[] {
			new Object[] {
				"href.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"hrefReturning.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"hrefVariable.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"hrefVariableAndString.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"src.jsp", "<img src=\"/params/contr/func\" />"
			},
			new Object[] {
				"srcReturning.jsp", "<img src=\"/params/contr/func\" />"
			},
			new Object[] {
				"srcVariable.jsp", "<img src=\"/params/contr/func\" />"
			},
			new Object[] {
				"srcVariableAndString.jsp", "<img src=\"/params/contr/func\" />"
			},
			new Object[] {
				"title.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"titleReturning.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"titleVariable.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"titleVariableAndString.jsp", "<div title=\"domain.some.key {}\">"
			},
			new Object[] {
				"alt.jsp", "<img alt=\"some.key {}\" />"
			},
			new Object[] {
				"altReturning.jsp", "<img alt=\"some.key {}\" />"
			},
			new Object[] {
				"altVariable.jsp", "<img alt=\"some.key {}\" />"
			},
			new Object[] {
				"altVariableAndString.jsp", "<img alt=\"domain.some.key {}\" />"
			},
			new Object[] {
				"placeholder.jsp", "<input placeholder=\"some.key {}\" />"
			},
			new Object[] {
				"placeholderReturning.jsp", "<input placeholder=\"some.key {}\" />"
			},
			new Object[] {
				"placeholderVariable.jsp", "<input placeholder=\"some.key {}\" />"
			},
			new Object[] {
				"placeholderVariableAndString.jsp", "<input placeholder=\"domain.some.key {}\" />"
			},
		};
	}
}
