package toti.env;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import toti.common.structures.MapInit;

public class JsonEnvSourceTest {

	@Test
	public void testLoad() throws IOException {
		Map<Object, Value> expected = new HashMap<>();
		expected.put("http", new Value(MapInit.create(Object.class, Value.class)
			.append("port", new Value(80))
			.append("pool", new Value(5))
			.append("ip", new Value(Arrays.asList(
				new Value(":::1"),
				new Value(":::2")
			)))
		.toMap()));
		expected.put("lang", new Value(MapInit.create(Object.class, Value.class)
			.append("en", new Value(MapInit.create(Object.class, Value.class)
				.append("name", new Value("EN"))
				.append("ltr", new Value(true))
				.append("subs", new Value(Arrays.asList(
					new Value(MapInit.create(Object.class, Value.class)
						.append("a", new Value("en_UK"))
						.append("b", new Value("UK"))
					.toMap())
				)))
			.toMap()))

			.append("cz", new Value(MapInit.create(Object.class, Value.class)
				.append("name", new Value("CZ"))
				.append("ltr", new Value(true))
				.append("subs", new Value(Arrays.asList(
					new Value(MapInit.create(Object.class, Value.class)
						.append("a", new Value("cs"))
						.append("b", new Value("CS"))
					.toMap()),
					new Value(MapInit.create(Object.class, Value.class)
						.append("a", new Value("cz"))
						.append("b", new Value("CZ"))
					.toMap())
				)))
			.toMap()))
		.toMap()));

		Map<Object, Value> actual = JsonEnvSource.parse("toti/env/env.json");
		try {
			assertEquals(expected, actual);
		} catch (Error e) {
			assertEquals(_toString(expected), _toString(actual));
		}
	}

	private String _toString(Map<Object, Value> vals) {
		StringBuilder res = new StringBuilder();
		vals.forEach((k, v)->{
			res.append(String.format("\n%s: %s", k, v.toString("")));
		});
		return res.toString();
	}

}
