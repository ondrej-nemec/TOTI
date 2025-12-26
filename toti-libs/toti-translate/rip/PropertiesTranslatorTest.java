package translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import common.Logger;
import common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import org.junit.jupiter.params.provider.MethodSource;

@RunWith(JUnitParamsRunner.class)
public class PropertiesTranslatorTest {
/*
	@Test
	public void testTranslateLoadFileFromClasspath() {
		PropertiesTranslator t = PropertiesTranslator.create(mock(Logger.class), "trans/mess-classpath");
		assertEquals("Translated message", t.translate("mess-classpath.translate.works", new HashMap<>(), Locale.getDefault()));
	}

	@Test
	public void testTranslateLoadFileFromDirTree() {
		PropertiesTranslator t = PropertiesTranslator.create(mock(Logger.class), "trans/mess-tree");
		assertEquals("Translated message", t.translate("mess-tree.translate.works", new HashMap<>(), Locale.getDefault()));
	}
*/
	@Test
	@MethodSource("dataTranslateReplaceVariable")
	public void testTranslateReplaceVariable(String expected, String key, Map<String, Object> params) {
		PropertiesTranslator t = PropertiesTranslator.create( mock(Logger.class), "messages");
		assertEquals(expected, t.translate(key, params, Locale.getDefault().toString()));
	}
	
	public Object[] dataTranslateReplaceVariable() {
		return new Object[] {
			new Object[] {
				"Translated message", "translate.works", new HashMap<>()
			},
			new Object[] {
				"Translated message", "translate-works", new HashMap<>()
			},
			new Object[] {
				"Variable: TEXT", "test.one.variable", new MapInit<>("variable", "TEXT").toMap()
			},
			new Object[] {
				"Variables: A, B", "test.two.variables", new MapInit<>("a", "A").append("b", "B").toMap()
			}
		};
	}

	@Test
	@MethodSource("dataTranslateUseCorrectLocale")
	public void testTranslateUseCorrectLocale(String expected, Locale locale) {
		PropertiesTranslator t = PropertiesTranslator.create(mock(Logger.class), "langs/messages");
		assertEquals(expected, t.translate("key", locale.toString()));
		assertEquals(expected, t.withLocale(new translator.Locale(locale.toString(), true, Arrays.asList())).translate("key"));
	}
	
	public Object[] dataTranslateUseCorrectLocale() {
		return new Object[] {
				new Object[] {
						"České znaky -> ěščřžýáíéůú", new Locale("cs")
					},
			new Object[] {
					"English text", new Locale("en")
			},
			new Object[] {
					"Русский текст", new Locale("ru")
				},
			new Object[] {
					"中文文字", new Locale("zh")
				},
			new Object[] {
					"Default", new Locale("missing")
				}
		};
	}

	@Test
	@MethodSource("dataTranslateSelectTransFileByPrefix")
	public void testTranslateSelectTransFileByPrefix(String key, String expected) {
		PropertiesTranslator t = PropertiesTranslator.create(
				mock(Logger.class),
				"modules/common", 
				"modules/module", 
				"modules/messages"
		);
		assertEquals(expected, t.translate(key));
	}
	
	public Object[] dataTranslateSelectTransFileByPrefix() {
		return new Object[] {
			new Object[] {
				"common.some.key", "Common text"
			},
			new Object[] {
				"module.some.key", "Module text"
			},
			new Object[] {
					"some.key", "Default value"
				}
		};
	}
	
}
