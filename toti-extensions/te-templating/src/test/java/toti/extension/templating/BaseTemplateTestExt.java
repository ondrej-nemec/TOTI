package toti.extension.templating;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import toti.answers.response.ResponseContainer;
import toti.application.register.MappedAction;
import toti.extensions.Translator;
import toti.lib.templating.BaseTemplateTest;

public class BaseTemplateTestExt extends BaseTemplateTest {

	public BaseTemplateTestExt(String path) {
		super(
			path,
			new TemplateResponseContainer(new ResponseContainer(
				new Translator() {
					@Override public String translate(String key, Map<String, Object> params) {
						return key + " " + params;
					}
					@Override public String translate(String key) {
						return translate(key, new HashMap<>());
					}
				},
				null,
				MappedAction.test("aa", "bb", "cc"),
				null,
				null
			)),
			new TemplateExtension("", false, false, mock(Logger.class)).getTags(),
			new TemplateExtension("", false, false, mock(Logger.class)).getParameters()
		);
	}

}
