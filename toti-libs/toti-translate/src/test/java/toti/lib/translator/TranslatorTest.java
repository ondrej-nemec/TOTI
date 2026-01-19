package toti.lib.translator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import toti.lib.common.structures.MapInit;

public class TranslatorTest {

	@ParameterizedTest
	@MethodSource("dataGetDataset")
	public void testGetDataset(String namespace, String lang, Map<Object, Object> expected) {
		Set<String> folders = new HashSet<>();
		folders.add("translator");

		Translator t = new Translator(folders);
		assertEquals(expected, t.getDataset(namespace, lang));
	}

	public static Object[] dataGetDataset() {
		return new Object[] {
			new Object[] {
				"messages", "", MapInit.create().append("bb", "BB").toMap()
			},
			new Object[] {
				"messages", "cs", MapInit.create().append("c", "C").append("d", "D").toMap()
			},
			new Object[] {
				"messages", "en", MapInit.create().append("b", "B").toMap()
			}
		};
	}

	@ParameterizedTest
	@MethodSource("dataTranslate")
	public void testTranslate(String key, String expected, String lang) {
		Set<String> folder = new HashSet<>();
		folder.add("translator");

		Translator t = new Translator("messages", lang, folder);
		assertEquals(expected, t.translate(key));
	}

	public static Object[] dataTranslate() {
		return new Object[] {
			new Object[] { "bb", "BB", "" },
			new Object[] { "messages:bb", "BB", "" },
			new Object[] { "common:aa", "common:aa", "" },
			new Object[] { "common:a", "A", "en" },
			new Object[] { "b", "B", "en" },
			new Object[] { "error:a", "error:a", "en" },
			new Object[] { "error:a", "error:a", "us" },
			new Object[] { "messages:c", "C", "cs" },
			new Object[] { "c", "C", "cs" }
		};
	}

	@ParameterizedTest
	@MethodSource("dataTranslate_Key")
	public void testTranslate_Key(String key, String expected) {
		Map<Object, Object> dataset = new HashMap<>();
		dataset.put("key1", "value");
		Translator t = new Translator(null);
		assertEquals(expected, t.translate("namespace", "lang", key, new HashMap<>(), dataset));
	}

	public static Object[] dataTranslate_Key() {
		return new Object[] {
			new Object[] { "key1", "value" },
			new Object[] { "key2", "namespace:key2" }
		};
	}

	@Test
	public void testTranslate_Dataset() {
		Map<Object, Object> vals = new HashMap<>();
		vals.put("a.b.c", 123);

		Translator t = spy(new Translator(null));
		doReturn(vals).when(t).getDataset(anyString(), anyString());

		assertEquals("123", t.translate("common", "lang", "a.b.c", new HashMap<>()));

		verify(t, times(1)).getDataset("common", "lang");
		verify(t, times(1)).translate("common", "lang", "a.b.c", new HashMap<>(), vals);
		verify(t, times(1)).replaceVariables("123", new HashMap<>());

		assertEquals("123", t.translate("common", "lang", "a.b.c", new HashMap<>()));

		verify(t, times(2)).translate("common", "lang", "a.b.c", new HashMap<>());
		verify(t, times(2)).translate("common", "lang", "a.b.c", new HashMap<>(), vals);
		verify(t, times(2)).replaceVariables("123", new HashMap<>());
		verifyNoMoreInteractions(t);
	}

	@ParameterizedTest
	@MethodSource("dataTranslate_SelectNamespace")
	public void testTranslate_SelectNamespace(String key, String expectedNamespace, String expectedKey) {
		Translator t = spy(new Translator("messages", null, null));
		doReturn("").when(t).translate(anyString(), anyString(), anyString(), any());

		t.translate("lang", key, new HashMap<>());

		verify(t, times(1)).translate(expectedNamespace, "lang", expectedKey, new HashMap<>());
	}

	public static Object[] dataTranslate_SelectNamespace() {
		return new Object[] {
			new Object[] { "common.a.b", "messages", "common.a.b" },
			new Object[] { "common:a.b", "common", "a.b" },
			new Object[] { "messages.a.b", "messages", "messages.a.b" },
			new Object[] { "common:errors:a.b", "common", "errors:a.b" }
		};
	}

	@ParameterizedTest
	@MethodSource("dataReplaceVariables")
	public void testReplaceVariables(String key, Map<String, Object> variables, String expected) {
		Translator t = new Translator(null);
		assertEquals(expected, t.replaceVariables(key, variables));
	}

	public static Object[] dataReplaceVariables() {
		return new Object[] {
			new Object[] {
				"with %nullable% var",
				MapInit.create().append("nullable", null).toMap(),
				"with  var"
			},
			new Object[] {
				"with %number% var",
				MapInit.create().append("number", 42).toMap(),
				"with 42 var"
			},
			new Object[] {
				"with %boolean% var",
				MapInit.create().append("boolean", true).toMap(),
				"with true var"
			},
			new Object[] {
				"with %string% var",
				MapInit.create().append("string", "some text").toMap(),
				"with some text var"
			},
			new Object[] {
				"with %someObject% var",
				MapInit.create().append("someObject", Arrays.asList(123, "xxx")).toMap(),
				"with [123, xxx] var"
			},
			new Object[] {
				"with %used% var",
				MapInit.create().append("not-used", 123).append("used", 456).toMap(),
				"with 456 var"
			},
			new Object[] {
				"multiple %var% -> %var%",
				MapInit.create().append("var", 123).toMap(),
				"multiple 123 -> 123"
			},
			new Object[] {
				"with %missing% var",
				MapInit.create().append("string", "some text").toMap(),
				"with %missing% var"
			},
			new Object[] {
				"with {string} var",
				MapInit.create().append("string", "some text").toMap(),
				"with {string} var"
			}
		};
	}

}
