package toti.tcpip.parsers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;
import toti.lib.common.structures.Tuple2;

public class PayloadTest {

	@ParameterizedTest
	@MethodSource("dataParseParamsWorks")
	public void testWriteWorks(List<Tuple2<String, Object>> expected, Map<String, Object> payload) throws UnsupportedEncodingException {
		Payload parser = new Payload();
		List<Tuple2<String, Object>> actual = new ArrayList<>();
		payload.forEach((name, value)->{
			parser.writeItem((n,v)->actual.add(new Tuple2<>(n, v)), name, value);
		});
		assertEquals(expected, actual);
	}

	@ParameterizedTest
	@MethodSource("dataParseParamsWorks")
	public void testReadWorks(List<Tuple2<String, Object>> payload, Map<String, Object> expected) throws UnsupportedEncodingException {
		Payload parser = new Payload();
		Map<String, Object> actual = new HashMap<>();
		payload.forEach((t)->{
			parser.readItem(
				(n,v)->actual.put(n, v), 
				n->actual.get(n),
				t._1(), t._2() + ""
			);
		});
		assertEquals(expected, actual);
	}
	
	public static Object[] dataParseParamsWorks() {
		// key=
		// key=value
		// key1=value1&key2=value2
		// list[]=value1
		// list[]=value1&list[]=value2
		// map[a]=value1
		// map[a]=value1&map[b]=value2
		// map[a][]=value
		// map[a][]=value1&map[a][]=value2
		// maplist[a][]=value-maplist-a-1&maplist[a][]=value-maplist-a-2&maplist[b][]=value-maplist-b-1&maplist[b][]=value-maplist-b-2&list[]=value-list-1&list[]=value-list-2&list[]=value-list-3&map[a]=value-map-a&map[b]=value-map-b
		return new Object[] {
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("key", "")
				),
				new MapInit<String, Object>()
					.append("key", "")
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("key", "value")
				),
				new MapInit<String, Object>()
					.append("key", "value")
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("key1", "value1"),
					new Tuple2<String, Object>("key2", "value2")
				),
				new MapInit<String, Object>()
					.append("key1", "value1")
					.append("key2", "value2")
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("list[]", "value1")
				),
				new MapInit<String, Object>()
					.append("list", Arrays.asList("value1"))
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("list[]", "value1"),
					new Tuple2<String, Object>("list[]", "value2")
				),
				new MapInit<String, Object>()
					.append("list", Arrays.asList("value1", "value2"))
					.toMap()
			},
			new Object[] {
				Arrays.asList(
						new Tuple2<String, Object>("map[a]", "value1")
				),
				new MapInit<String, Object>()
					.append(
						"map", 
						new MapInit<String, Object>()
						.append("a", "value1")
						.toMap()
					)
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("map[a]", "value1"),
					new Tuple2<String, Object>("map[b]", "value2")
				),
				new MapInit<String, Object>()
					.append(
						"map",
						new MapInit<>()
						.append("a", "value1")
						.append("b", "value2")
						.toMap()
					)
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("map[a][]", "value")
				),
				new MapInit<String, Object>()
					.append(
						"map",
						new MapInit<>("a", Arrays.asList("value")).toMap()
					)
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("map[a][]", "value1"),
					new Tuple2<String, Object>("map[a][]", "value2")
				),
				new MapInit<String, Object>()
					.append(
						"map",
						new MapInit<>("a", Arrays.asList("value1", "value2")).toMap()
					)
					.toMap()
			},
			new Object[] {
				Arrays.asList(
					new Tuple2<String, Object>("maplist[a][]", "value-maplist-a-1"),
					new Tuple2<String, Object>("maplist[a][]", "value-maplist-a-2"),
					new Tuple2<String, Object>("maplist[b][]", "value-maplist-b-1"),
					new Tuple2<String, Object>("maplist[b][]", "value-maplist-b-2"),
					new Tuple2<String, Object>("list[]", "value-list-1"),
					new Tuple2<String, Object>("list[]", "value-list-2"),
					new Tuple2<String, Object>("list[]", "value-list-3"),
					new Tuple2<String, Object>("map[a]", "value-map-a"),
					new Tuple2<String, Object>("map[b]", "value-map-b")
				),
				new MapInit<String, Object>()
					.append(
						"list", 
						Arrays.asList(
							"value-list-1", 
							"value-list-2", 
							"value-list-3"
						)
					)
					.append(
						"map",
						new MapInit<>()
						.append("a", "value-map-a")
						.append("b", "value-map-b")
						.toMap()
					)
					.append(
						"maplist",
						new MapInit<>()
						.append(
							"a", 
							Arrays.asList(
								"value-maplist-a-1", 
								"value-maplist-a-2"
							)
						)
						.append(
							"b", 
							Arrays.asList(
								"value-maplist-b-1", 
								"value-maplist-b-2"
							)
						)
						.toMap()
					)
					.toMap(),
			}
		};
	}
	
}
