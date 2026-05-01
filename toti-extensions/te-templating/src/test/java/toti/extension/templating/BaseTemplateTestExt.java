package toti.extension.templating;

import java.util.HashMap;
import java.util.Map;

import toti.application.application.register.MappedAction;
import toti.application.extensions.Translator;
import toti.lib.templating.BaseTemplateTest;

public class BaseTemplateTestExt extends BaseTemplateTest {

	public BaseTemplateTestExt() {
		super();
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
