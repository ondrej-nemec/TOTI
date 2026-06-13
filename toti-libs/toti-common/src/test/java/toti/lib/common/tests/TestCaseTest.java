package toti.lib.common.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.structures.MapInit;

public class TestCaseTest {

	@ParameterizedTest
	@MethodSource
	public void testToString_map(String prefix, Map<?, ?> map, String expected) {
		assertEquals(expected, TestCase._toString(map, prefix));
	}

	public static Object[] testToString_map() {
		return new Object[] {
			new Object[] {
				"", null, "NULL"
			},
			new Object[] {
				"", MapInit.create().toMap(), "{}"
			},
			new Object[] {
				"  ", MapInit.create().toMap(), "{}"
			},
			new Object[] {
				"", MapInit.create().append("a", "A").toMap(),
"""
{
  a: A
}"""
			},
			new Object[] {
				"", MapInit.create().append("a", "A").append("b", "B").toMap(),
"""
{
  a: A
  b: B
}"""
			},
			new Object[] {
				"  ", MapInit.create().append("a", "A").append("b", "B").toMap(),
"""
{
    a: A
    b: B
  }"""
			}
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testToString_list(String prefix, List<?> list, String expected) {
		assertEquals(expected, TestCase._toString(list, prefix));
	}

	public static Object[] testToString_list() {
		return new Object[] {
			new Object[] {
				"", null, "NULL"
			},
			new Object[] {
				"", Arrays.asList(), "[]"
			},
			new Object[] {
				"  ", Arrays.asList(), "[]"
			},
			new Object[] {
				"", Arrays.asList("a"),
"""
[
  a
]"""
			},
			new Object[] {
				"", Arrays.asList("a", "b"),
"""
[
  a
  b
]"""
			},
			new Object[] {
				"  ", Arrays.asList("a", "b"),
"""
[
    a
    b
  ]"""
			}
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testToString_value(String prefix, Object value, String expected) {
		assertEquals(expected, TestCase._toString(value, prefix));
	}

	public static Object[] testToString_value() {
		return new Object[] {
			new Object[] {
				"", null, "NULL"
			},
			new Object[] {
				"", 1, "1"
			},
			new Object[] {
				"", "aaa", "aaa"
			},
			new Object[] {
				"", "'aaa'", "'aaa'"
			},
			new Object[] {
				"", "\"aaa\"", "\"aaa\""
			},
			new Object[] {
				"", Arrays.asList(
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
				"  ", Arrays.asList(
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
				"", MapInit.create()
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
				"  ", MapInit.create()
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
		};
	}

}
