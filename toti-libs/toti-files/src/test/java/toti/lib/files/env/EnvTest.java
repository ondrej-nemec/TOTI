package toti.lib.files.env;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import toti.lib.common.structures.MapInit;
import toti.lib.files.env.Env;
import toti.lib.files.env.Value;

public class EnvTest {

	@Test
	public void testGetSection() {
		Env env = getEnv();
		assertEquals(
			new Env(MapInit.create(Object.class, Value.class)
				.append("sub-1", new Value("aaa"))
				.append("sub-2", new Value(false))
			.toMap()),
			env.getSection("object-of-scalars")
		);
	}

	@Test
	public void testGetNotExistingSection() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getSection("wrong"));
		assertEquals("'wrong' is not a section", expected.getMessage());
	}

	@Test
	public void testGetSectionButValue() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getSection("value-a"));
		assertEquals("'value-a' is not a section", expected.getMessage());
	}

	@Test
	public void testGetSectionButList() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getSection("list-of-scalars"));
		assertEquals("'list-of-scalars' is not a section", expected.getMessage());
	}

	@Test
	public void testGetValue() {
		Env env = getEnv();
		assertEquals(2, env.getValue("value-b"));
	}

	@Test
	public void testGetNotExistingValue() {
		Env env = getEnv();
		assertNull(env.getValue("wrong"));
	}

	@Test
	public void testGetValueButObject() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getValue("object-of-scalars"));
		assertEquals("'object-of-scalars' is not a value", expected.getMessage());
	}

	@Test
	public void testGetValueButList() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getValue("list-of-scalars"));
		assertEquals("'list-of-scalars' is not a value", expected.getMessage());
	}

	@Test
	public void getList() {
		Env env = getEnv();
		assertEquals(
			Arrays.asList(
				new Value("123"),
				new Value(456)
			),
			env.getList("list-of-scalars")
		);
	}

	@Test
	public void getNotExistingList() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getList("wrong"));
		assertEquals("'wrong' is null or value", expected.getMessage());
	}

	@Test
	public void getListButValue() {
		Env env = getEnv();
		RuntimeException expected = assertThrows(RuntimeException.class, ()->env.getList("value-a"));
		assertEquals("'value-a' is null or value", expected.getMessage());
	}

	@Test
	public void getListButObject() {
		Env env = getEnv();
		assertEquals(
			Arrays.asList(
				new Value(MapInit.create(Object.class, Value.class)
					.append("sub-1", new Value("aaa"))
					.append("sub-2", new Value(false))
				.toMap())
			),
			env.getList("object-of-scalars")
		);
	}

	@Test
	public void testIterate() {
		Env env = getEnv();
		StringBuilder result = new StringBuilder();
		env.getSection("object-of-scalars").iterate((n, v)->{
			result.append(String.format("%s %s\n", n, v));
		});
		assertEquals(
			"sub-2 Value (Val) {false}\n"
			+ "sub-1 Value (Val) {aaa}\n",
			result.toString());
	}


	private Env getEnv() {
		return new Env(MapInit.create(Object.class, Value.class)
			.append("value-a", new Value("something"))
			.append("value-b", new Value(2))
			.append("object-of-scalars", new Value(MapInit.create(Object.class, Value.class)
				.append("sub-1", new Value("aaa"))
				.append("sub-2", new Value(false))
			.toMap()))
			.append("list-of-scalars", new Value(Arrays.asList(
				new Value("123"),
				new Value(456)
			)))
			.append("object-of-objects", new Value(MapInit.create(Object.class, Value.class)
				.append("A", new Value(new Value(MapInit.create(Object.class, Value.class)
					.append("A1", new Value(1))
					.append("A2", new Value(2))
				.toMap())))
				.append("B", new Value(new Value(MapInit.create(Object.class, Value.class)
					.append("B1", new Value(1))
					.append("B2", new Value(2))
				.toMap())))
			.toMap()))
			.append("list-of-objects", new Value(Arrays.asList(
				new Value(new Value(MapInit.create(Object.class, Value.class)
					.append("X1", new Value(1))
					.append("X2", new Value(2))
				.toMap())),
				new Value(new Value(MapInit.create(Object.class, Value.class)
					.append("Y1", new Value(1))
					.append("Y2", new Value(2))
				.toMap()))
			)))
		.toMap());
	}

}
