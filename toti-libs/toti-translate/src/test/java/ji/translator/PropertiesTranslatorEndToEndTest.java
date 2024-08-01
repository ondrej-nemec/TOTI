package ji.translator;

import java.util.Arrays;
import java.util.HashMap;

import ji.common.Log4j2LoggerTestImpl;
import ji.common.structures.ListInit;

public class PropertiesTranslatorEndToEndTest {

	public static void main(String[] args) {
		PropertiesTranslator translator = new PropertiesTranslator(
			new LanguageSettings( Arrays.asList(
				new Locale("", true, Arrays.asList()),
				new Locale("cs", true, Arrays.asList("cs_CZ")),
				new Locale("ru", true, Arrays.asList()),
				new Locale("zh", true, Arrays.asList()),
				new Locale("en", true, Arrays.asList())
			)),
			new ListInit<String>().add("modules").add("modules2").add("langs").toSet(),
			new Log4j2LoggerTestImpl(null)
		);
		
		System.out.println(translator.translate("common.some.key"));
		System.out.println(translator.translate("common.some2.key"));
		System.out.println(translator.translate("common.some.key"));
		System.out.println(translator.translate("common.some2.key"));
		System.out.println("---------");
		System.out.println(translator.translate("messages.key", new HashMap<>(), "cs"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "cs_CZ"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "en"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "ru"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "zh"));
		System.out.println(translator.translate("messages.key"));
		System.out.println("---------");
		System.out.println(translator.translate("messages.key", new HashMap<>(), "cs"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "cs_CZ"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "en"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "ru"));
		System.out.println(translator.translate("messages.key", new HashMap<>(), "zh"));
		System.out.println(translator.translate("messages.key"));
		
	}
	
}
