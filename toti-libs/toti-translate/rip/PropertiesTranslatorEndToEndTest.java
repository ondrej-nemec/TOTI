package translator;

import java.util.Arrays;

public class PropertiesTranslatorEndToEndTest {

	public static void main(String[] args) {
		/*
		for (Locale l : new Locale[] {
				Locale.getDefault(), 
				new Locale("cs", "CZ"),
				new Locale("cs"), 
				Locale.forLanguageTag("cs-CZ"),
				Locale.forLanguageTag("cs_CZ"),
				Locale.forLanguageTag("cs")
			}) {
			System.out.println("Locale");
			System.out.println(l);
			System.out.println(
				"Contry: '" + l.getCountry() + "' '" + l.getDisplayCountry() + "' '" + l.getISO3Country() + "'"
			);
			System.out.println(
				"Lang: " + l.getLanguage() + "' '" + l.getDisplayLanguage() + "' '" + l.getISO3Language() + "'"
			);
			System.out.println("tag: " + l.toLanguageTag());
			System.out.println();
		}
		/*/
		PropertiesTranslator t = PropertiesTranslator.create(new LoggerImpl(), "langs/messages");
		System.out.println(t.translate("key", "cs"));
		System.out.println(t.translate("key", "cs"));
		Translator t2 = t.withLocale(new Locale("en", true, Arrays.asList()));
		System.out.println(t2.translate("key"));
		System.out.println(t2.translate("key", "cs"));
		System.out.println(t.translate("key", "en"));
		//*/
	}

}
