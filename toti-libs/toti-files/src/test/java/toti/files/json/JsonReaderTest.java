package toti.files.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import toti.lib.common.structures.MapInit;

public class JsonReaderTest {
	
	@Test
	public void testReadWorks() throws JsonStreamException {
		Map<String, Object> expected = new HashMap<>();
		expected.put("value", 123);
		expected.put("list", Arrays.asList(1,2,3));
		expected.put("object", new MapInit<String, Object>().append("a", null).append("b", "aaa").toMap());
		String json = "{\"value\":123, \"list\":[1,2,3],\"object\":{\"a\":null, \"b\": \"aaa\"}}";
		assertEquals(expected, new JsonReader().read(json));
	//	assertEquals(expected, new JsonReader().read(json));
	}

	@Test
	public void testReadListOfStrings() throws JsonStreamException {
		Map<String, Object> expected = new HashMap<>();
		expected.put("list", Arrays.asList("a","b","c"));
		String json = "{\"list\":[\"a\",\"b\",\"c\"]}";
		assertEquals(expected, new JsonReader().read(json));
	//	assertEquals(expected, new JsonReader().read(json));
	}

	@Test
	public void testReadWorksWithList() throws JsonStreamException {
		String json = "[1,2,3]";
		assertEquals(Arrays.asList(1,2,3), new JsonReader().read(json));
	}
	
}
