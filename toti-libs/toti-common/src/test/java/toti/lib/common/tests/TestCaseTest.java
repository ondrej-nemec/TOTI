package toti.lib.common.tests;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;

public class TestCaseTest {

	@ParameterizedTest
	@MethodSource
	public void testToString_map(String prefix, boolean withClass, Map<?, ?> map, String expected) {
		assertEquals(expected, TestCase._toString(map, prefix, withClass));
	}

	public static Object[] testToString_map() {
		return new Object[] {
			new Object[] {
				"", false, null, "NULL"
			},
			new Object[] {
				"", false, MapInit.create().toMap(), "{}"
			},
			new Object[] {
				"  ", false, MapInit.create().toMap(), "{}"
			},
			new Object[] {
				"", false, MapInit.create().append("a", "A").toMap(),
"""
{
  a: A
}"""
			},
			new Object[] {
				"", false, MapInit.create().append("a", "A").append("b", "B").toMap(),
"""
{
  a: A
  b: B
}"""
			},
			new Object[] {
				"  ", false, MapInit.create().append("a", "A").append("b", "B").toMap(),
"""
{
    a: A
    b: B
  }"""
			},
			new Object[] {
				"", true, null, "NULL"
			},
			new Object[] {
				"", true, MapInit.create().append("a", "A").append("b", "B").toMap(),
"""
(java.util.HashMap) {
  a: A (java.lang.String)
  b: B (java.lang.String)
}"""
			},
		};
	}


	@ParameterizedTest
	@MethodSource
	public void testToString_array(String prefix, boolean withClass, Object[] array, String expected) {
		assertEquals(expected, TestCase._toString(array, prefix, withClass));
	}

	public static Object[] testToString_array() {
		return new Object[] {
			new Object[] {
				"", false, null, "NULL"
			},
			new Object[] {
				"", false, new String[] {}, "[]"
			},
			new Object[] {
				"", false, new String[] {"a", "b"},
"""
[
  a
  b
]"""
			},
			new Object[] {
				"  ", false, new String[] {"a", "b"}, 
"""
[
    a
    b
  ]"""
			},
			new Object[] {
				"", true, new String[] {"a", "b"},
"""
(java.util.Arrays.ArrayList) [
  a (java.lang.String)
  b (java.lang.String)
]"""
			},

		};
	}

	@ParameterizedTest
	@MethodSource
	public void testToString_list(String prefix, boolean withClass, Iterable<?> list, String expected) {
		assertEquals(expected, TestCase._toString(list, prefix, withClass));
	}

	public static Object[] testToString_list() {
		return new Object[] {
			new Object[] {
				"", false, null, "NULL"
			},
			new Object[] {
				"", false, Arrays.asList(), "[]"
			},
			new Object[] {
				"  ", false, Arrays.asList(), "[]"
			},
			new Object[] {
				"", false, Arrays.asList("a"),
"""
[
  a
]"""
			},
			new Object[] {
				"", false, Arrays.asList("a", "b"),
"""
[
  a
  b
]"""
			},
			new Object[] {
				"  ", false, Arrays.asList("a", "b"),
"""
[
    a
    b
  ]"""
			},
			new Object[] {
				"", true, null, "NULL"
			},
			new Object[] {
				"", true, Arrays.asList(), "(java.util.Arrays.ArrayList) []"
			},
			new Object[] {
				"", true, Arrays.asList("a"),
"""
(java.util.Arrays.ArrayList) [
  a (java.lang.String)
]"""
			},
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testToString_value(String prefix, boolean withClass, Object value, String expected) {
		assertEquals(expected, TestCase._toString(value, prefix, withClass));
	}

	public static Object[] testToString_value() {
		return new Object[] {
			new Object[] {
				"", false, null, "NULL"
			},
			new Object[] {
				"", false, 1, "1"
			},
			new Object[] {
				"", false, "aaa", "aaa"
			},
			new Object[] {
				"", false, "'aaa'", "'aaa'"
			},
			new Object[] {
				"", false, "\"aaa\"", "\"aaa\""
			},
			new Object[] {
				"", false, Arrays.asList(
					MapInit.create().append("a", "A").append("b", "B").toMap(),
					MapInit.create().append("c", "C").append("d", "D").toMap()
				),
"""
[
  {
    a: A
    b: B
  }
  {
    c: C
    d: D
  }
]"""
			},
			new Object[] {
				"  ", false, Arrays.asList(
					MapInit.create().append("a", "A").append("b", "B").toMap(),
					MapInit.create().append("c", "C").append("d", "D").toMap()
				),
"""
[
    {
      a: A
      b: B
    }
    {
      c: C
      d: D
    }
  ]"""
			},
			new Object[] {
				"", false, MapInit.create()
				.append("a", Arrays.asList("A1", "A2"))
				.append("b", Arrays.asList("B1", "B2"))
				.toMap(),
"""
{
  a: [
    A1
    A2
  ]
  b: [
    B1
    B2
  ]
}"""
			},
			new Object[] {
				"  ", false, MapInit.create()
				.append("a", Arrays.asList("A1", "A2"))
				.append("b", Arrays.asList("B1", "B2"))
				.toMap(),
"""
{
    a: [
      A1
      A2
    ]
    b: [
      B1
      B2
    ]
  }"""
			},
			new Object[] {
				"", true, null, "NULL"
			},
			new Object[] {
				"", true, 1, "1 (java.lang.Integer)"
			},
			new Object[] {
				"", true, "aaa", "aaa (java.lang.String)"
			},
			new Object[] {
				"", true, Arrays.asList(
					MapInit.create().append("a", "A").append("b", "B").toMap(),
					MapInit.create().append("c", "C").append("d", "D").toMap()
				),
"""
(java.util.Arrays.ArrayList) [
  (java.util.HashMap) {
    a: A (java.lang.String)
    b: B (java.lang.String)
  }
  (java.util.HashMap) {
    c: C (java.lang.String)
    d: D (java.lang.String)
  }
]"""
			},
		};
	}

}
