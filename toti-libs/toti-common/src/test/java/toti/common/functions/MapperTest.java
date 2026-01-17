package toti.common.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import toti.common.annotations.MapperIgnored;
import toti.common.annotations.MapperParameter;
import toti.common.annotations.MapperType;
import toti.common.functions.Mapper;
import toti.common.functions.testingClasses.Generic;
import toti.common.functions.testingClasses.Main;
import toti.common.functions.testingClasses.Parse;
import toti.common.structures.MapInit;

public class MapperTest {

	@Test
	public void testSerializeAddAllParameters() {
		Object actual = new Object() {
			@SuppressWarnings("unused")
			private String first = "first value";
			@SuppressWarnings("unused")
			private int second = 42;
			@SuppressWarnings("unused")
			private List<String> list = Arrays.asList("a", "b", "c");
		};
		Map<String, Object> expected = new MapInit<String, Object>()
				.append("first", "first value")
				.append("second", 42)
				.append("list", Arrays.asList("a", "b", "c"))
				.toMap();
		assertEquals(expected, Mapper.get().serialize(actual));
	}

	@Test
	public void testSerializeAnnotatedAttributes() {
		Object actual = new Object() {
			@SuppressWarnings("unused")
			private String first = "first value";
			@SuppressWarnings("unused")
			private int second = 42;
			@SuppressWarnings("unused")
			private List<String> list = Arrays.asList("a", "b", "c");
			@MapperIgnored
			private double ignored = 12.3;
			@MapperParameter({@MapperType("realName")})
			private String anotherName = "renamed";
		};
		Map<String, Object> expected = new MapInit<String, Object>()
				.append("first", "first value")
				.append("second", 42)
				.append("list", Arrays.asList("a", "b", "c"))
				.append("realName", "renamed")
				.toMap();
		assertEquals(expected, Mapper.get().serialize(actual));
	}

	@Test
	public void testSerializeAnnotatedAttributesWithSwitch() {
		Object actual = new Object() {
			@SuppressWarnings("unused")
			private String first = "first value";
			@MapperParameter({@MapperType(value = "second", key = "used")})
			private int second = 42;
			@MapperIgnored({"used"})
			@MapperParameter({@MapperType(value = "--list--", key = "not-used")})
			private List<String> list = Arrays.asList("a", "b", "c");
			@MapperIgnored
			private double ignored = 12.3;
			@MapperParameter({@MapperType(value="NullParameter", ignoreOnNull = true)})
			private String nullParam = null;
			@MapperParameter({@MapperType("realName")})
			private String anotherName = "renamed";
		};
		Map<String, Object> expected = new MapInit<String, Object>()
				.append("first", "first value")
				.append("second", 42)
			//	.append("--list--", Arrays.asList("a", "b", "c"))
				.append("realName", "renamed")
				.toMap();
		assertEquals(expected, Mapper.get().serialize(actual, "used"));
	}
	
	@Test
	public void testParseObject() throws Exception {
		assertEquals(new Main(true), Mapper.get().parse(
			Main.class, 
			new MapInit<String, Object>()
				.append("first", 42)
				.append("second", "Hello World!")
				.append("list", Arrays.asList(
					"aaa",
					95/*,
					new MapInit<String, Object>()
						.append("text1", "text1")
						.append("text2", "text2")
						.toMap()*/
				))
				.append("map", new MapInit<String, Object>()
					.append("item1", 123)
					.append("item2", true)
					.toMap()
				)
				.append("sub1", new MapInit<String, Object>()
						.append("text1", "text1")
						.append("text2", "text2")
						.toMap()
				)
				.append("subs", Arrays.asList(new MapInit<String, Object>()
						.append("text1", "text1")
						.append("text2", "text2")
						.toMap()))
				.toMap())
		);
	}
	
	@Test
	public void testParseGenericInGeneric() throws Exception {
		assertEquals(new Generic(true), Mapper.get().parse(
			Generic.class, 
			new MapInit<String, Object>()
				.append(
					"generic",
					Arrays.asList(
						new MapInit<String, String>()
						.append("a", "b")
						.toMap()
					)
				)
				.toMap()
			)
		);
	}

	@Test
	public void testParseAnnotation() throws Exception {
		Map<String, Object> actual = new MapInit<String, Object>()
				.append("first", "first value")
				.append("second", 42)
				.append("--list--", Arrays.asList("a", "b", "c"))
				.append("realName", "renamed")
				.toMap();
		assertEquals(new Parse("first value", 42, "renamed"), Mapper.get().parse(Parse.class, actual, "used"));
	}
}
