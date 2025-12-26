package toti.extension.templating.parameters;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ji.common.structures.MapInit;
import toti.extension.templating.BaseTemplateTestExt;

public class ParametersEndToEndTest extends BaseTemplateTestExt {

	public ParametersEndToEndTest() {
		super("toti/extension/templating/parameters");
	}
	
	@ParameterizedTest
	@MethodSource("data")
	public void test(String template, String expected) throws Exception {
		Map<String, Object> variables = new MapInit<String, Object>()
			//	.append("totiIdentity", identity)
				.append("toTranslate", "some.key")
				.append("fullLink", "toti.templating.parameters.Controller:index")
				.append("method", "index")
				.toMap();
		testTemplate(variables, "", template, expected);
	}
	
	public static Object[] data() {
		return new Object[] {
				// TODO alt, src, placeholder
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
			}
		};
	}
}
