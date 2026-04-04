package toti.extension.templating;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import static org.mockito.Mockito.mock;

import toti.application.application.register.MappedAction;
import toti.application.extensions.Translator;
import toti.lib.templating.BaseTemplateTest;

public class BaseTemplateTestExt extends BaseTemplateTest {

	public BaseTemplateTestExt() {
		super(
			new TemplateExtension("", false, false, mock(Logger.class)).getTags(),
			new TemplateExtension("", false, false, mock(Logger.class)).getParameters()
		);
	}

	protected Translator getTranslator() {
		return new Translator() {
			@Override public String translate(String key, Map<String, Object> params) {
				return key + " " + params;
			}
			@Override public String translate(String key) {
				return translate(key, new HashMap<>());
			}
		};
	}

	public MappedAction getAction() {
		return MappedAction.test("aa", "bb", "cc");
	}

}
