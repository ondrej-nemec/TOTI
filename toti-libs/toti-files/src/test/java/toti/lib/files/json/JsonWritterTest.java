package toti.lib.files.json;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.files.json.JsonWritter;

public class JsonWritterTest {

	@ParameterizedTest
	@MethodSource("dataWriteJson")
	public void testWriteJson(Object json, String expected) {
		JsonWritter w = new JsonWritter();
		assertEquals(expected, w.write(json));
	}
	
	public static Object[] dataWriteJson() {
		// TODO add enums, jsonable, mapDictionary, listDictionary, Dictionaryvalue, optional,tuples...
		return new Object[] {
			new Object[] {
				MapInit.create().toMap(),
				"{}"
			},
			new Object[] {
				Arrays.asList(),
				"[]"
			},
			new Object[] {
				MapInit.create()
				.append("list", Arrays.asList(1, "a", null))
				.toMap(),
				"{\"list\":[1,\"a\",null]}"
			},
			new Object[] {
				Arrays.asList(
					MapInit.create()
					.append("a", "b")
					.append("c", 12)
					.toMap()
				),
				"[{\"a\":\"b\",\"c\":12}]"
			},
			new Object[] {
				MapInit.create()
				.append("bool", true)
				.append("text", "another-text")
				.append("int", 1)
				.append("double", 2.2)
				.append("null", null)
				.append("list", Arrays.asList("some-text"))
				.append("map", MapInit.create().append("a", "b").toMap())
				.toMap(),
				"{"
				+ "\"bool\":true,"
				+ "\"null\":null,"
				+ "\"double\":2.2,"
				+ "\"text\":\"another-text\","
				+ "\"list\":[\"some-text\"],"
				+ "\"map\":{\"a\":\"b\"},"
				+ "\"int\":1"
				+ "}"
			}
		};
	}
}
