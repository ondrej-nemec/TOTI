package toti.lib.common.tests;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import toti.lib.common.structures.ThrowingFunction;

public interface TestCase {
	
	static final String PREFIX = "  ";

	static <T> Consumer<T> consumer(Class<T> clazz, Consumer<T> consumer) {
		return consumer;
	}

	static <T> Supplier<T> supplier(Supplier<T> supplier) {
		return supplier;
	}

	static <T, R> Function<T, R> function(Function<T, R> function) {
		return function;
	}

	static <T, R, E extends Exception> ThrowingFunction<T, R, E> throwingFunction(ThrowingFunction<T, R, E> function) {
		return function;
	}
	
	/**************************/

	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual) {
		assertEquals(expected, actual, null);
	}
	
	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual, String message) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}
	
	static void assertEquals(Collection<?> expected, Collection<?> actual) {
		assertEquals(expected, actual, null);
	}

	static void assertEquals(Collection<?> expected, Collection<?> actual, String message) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}

	static void assertEquals(Object expected, Object actual) {
		assertEquals(expected, actual, null);
	}

	static void assertEquals(Object expected, Object actual, String message) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}
	
	/*********************************/

	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual) {
		assertNotEquals(expected, actual, null);
	}
	
	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual, String message) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}
	
	static void assertNotEquals(Collection<?> expected, Collection<?> actual) {
		assertEquals(expected, actual, null);
	}

	static void assertNotEquals(Collection<?> expected, Collection<?> actual, String message) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}

	static void assertNotEquals(Object expected, Object actual) {
		assertNotEquals(expected, actual, null);
	}

	static void assertNotEquals(Object expected, Object actual, String message) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected), _toString(actual), message);
			throw e;
		}
	}

	/******************************/

	static <K, V> String _toString(Map<K, V> map) {
		if (map == null) {
			return "NULL";
		}
		return _toString(map, "");
	}

	static <K, V> String _toString(Map<K, V> map, String prefix) {
		System.out.println("To string MAP '" + prefix + "'");
		StringBuilder res = new StringBuilder();
		res.append(prefix);
		res.append("{");
		map.forEach((k, v)->{
			res.append("\n");
			res.append(prefix);
			res.append(PREFIX);
			res.append(k);
			res.append(": ");
			res.append(_toString(v, prefix + PREFIX));
		});
		res.append("\n");
		res.append(prefix);
		res.append("}");
		return res.toString();
	}

	static <T> String _toString(Iterable<T> iterable) {
		if (iterable == null) {
			return "NULL";
		}
		return _toString(iterable, "");
	}

	static <T> String _toString(Iterable<T> iterable, String prefix) {
		StringBuilder res = new StringBuilder();
		res.append(prefix);
		res.append("[");
		for (T t : iterable) {
			res.append("\n");
			res.append(prefix);
			res.append(PREFIX);
			res.append(_toString(t, prefix + PREFIX));
		}
		res.append("\n");
		res.append(prefix);
		res.append("]");
		return res.toString();
	}

	static <T> String _toString(T t) {
		return _toString(t, "");
	}

	@SuppressWarnings("unchecked")
	static <T> String _toString(T t, String prefix) {
		if (t == null) {
			return prefix + "NULL ";
		}
		if (t instanceof Map) {
			return _toString(Map.class.cast(t), prefix);
		}
		if (t instanceof Iterable) {
			return _toString(Iterable.class.cast(t), prefix);
		}
		return t.toString();
	}
}
