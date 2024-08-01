package ji.translator;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.mockito.Mockito;

import org.apache.logging.log4j.Logger;

public class PropertiesTranslatorTest {

	@Test
	public void testTranslateSimple() {
		PropertiesTranslator translator = new PropertiesTranslator(
			new LanguageSettings("", Arrays.asList()),
			new HashSet<>(Arrays.asList("modules", "modules2", "langs")),
			Mockito.mock(Logger.class)
		);
		assertEquals("Common text", translator.translate("common.some.key"));
		assertEquals("Common text 2", translator.translate("common.some2.key"));
		assertEquals("Default", translator.translate("messages.key"));
	}
/*
	@Test
	public void testTranslateMoreLanguages() {
		LocaleTranslator translator = new LocaleTranslator(
			new LanguageSettings("", Arrays.asList(
				new Locale("cs", true, Arrays.asList())
			)),
			Arrays.asList("modules", "modules2", "langs"),
			Mockito.mock(Logger.class)
		);
		assertEquals("common.some.key", translator.translate("common.some.key"));
		assertEquals("common.some2.key", translator.translate("common.some2.key"));
		assertEquals("Default", translator.translate("messages.key"));
	}
*/	
}
