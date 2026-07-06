package toti.lib.common.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import toti.lib.common.structures.Callback;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.ThrowingBiConsumer;
import toti.lib.common.structures.ThrowingBiFunction;
import toti.lib.common.structures.ThrowingCallback;
import toti.lib.common.structures.ThrowingConsumer;
import toti.lib.common.structures.ThrowingFunction;
import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.common.structures.ThrowingTriConsumer;

public interface TestCase {
	
	static final String PREFIX = "  ";

	static <T> Consumer<T> consumer(Class<T> clazz, Consumer<T> consumer) {
		return consumer;
	}

	static <T, E extends Throwable> ThrowingConsumer<T, E> throwingConsumer(ThrowingConsumer<T, E> consumer) {
		return consumer;
	}

	static <T, S, E extends Throwable> ThrowingBiConsumer<T, S, E> throwingBiConsumer(ThrowingBiConsumer<T, S, E> biConsumer) {
		return biConsumer;
	}

	static <T, S, V, E extends Throwable> ThrowingTriConsumer<T, S, V, E> throwingTriConsumer(ThrowingTriConsumer<T, S, V, E> triConsumer) {
		return triConsumer;
	}
	
	static <T> Supplier<T> supplier(Supplier<T> supplier) {
		return supplier;
	}

	static <T, E extends Throwable> ThrowingSupplier<T, E> throwingSupplier(ThrowingSupplier<T, E> supplier) {
		return supplier;
	}

	static <T, R> Function<T, R> function(Function<T, R> function) {
		return function;
	}

	static <T, R, E extends Throwable> ThrowingFunction<T, R, E> throwingFunction(ThrowingFunction<T, R, E> function) {
		return function;
	}

	static <T, S, R, E extends Throwable> ThrowingBiFunction<T, S, R, E> throwingBiFunction(ThrowingBiFunction<T, S, R, E> biFunction) {
		return biFunction;
	}

	static Callback callback(Callback callback) {
		return callback;
	}

	static <E extends Throwable> ThrowingCallback<E> throwingCallback(ThrowingCallback<E> callback) {
		return callback;
	}
	
	/**************************/

	//#region

	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual) {
		assertEquals(expected, actual, null, false);
	}
	
	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual, String message) {
		assertEquals(expected, actual, message, false);
	}

	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual, boolean withClass) {
		assertEquals(expected, actual, null, withClass);
	}
	
	static void assertEquals(Map<?, ?> expected, Map<?, ?> actual, String message, boolean withClass) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}
	
	//#endregion
	//#region

	static void assertEquals(Collection<?> expected, Collection<?> actual) {
		assertEquals(expected, actual, null, false);
	}

	static void assertEquals(Collection<?> expected, Collection<?> actual, String message) {
		assertEquals(expected, actual, message, false);
	}

	static void assertEquals(Collection<?> expected, Collection<?> actual, boolean withClass) {
		assertEquals(expected, actual, null, withClass);
	}

	static void assertEquals(Collection<?> expected, Collection<?> actual, String message, boolean withClass) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}

	//#endregion
	//#region

	static void assertEquals(Object expected, Object actual) {
		assertEquals(expected, actual, null, false);
	}

	static void assertEquals(Object expected, Object actual, String message) {
		assertEquals(expected, actual, message, false);
	}

	static void assertEquals(Object expected, Object actual, boolean withClass) {
		assertEquals(expected, actual, null, withClass);
	}

	static void assertEquals(Object expected, Object actual, String message, boolean withClass) {
		try {
			Assertions.assertEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}

	//#endregion
	
	/*********************************/

	//#region

	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual) {
		assertNotEquals(expected, actual, null, false);
	}
	
	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual, String message) {
		assertNotEquals(expected, actual, message, false);
	}

	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual, boolean withClass) {
		assertNotEquals(expected, actual, null, withClass);
	}
	
	static void assertNotEquals(Map<?, ?> expected, Map<?, ?> actual, String message, boolean withClass) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}
	
	//#endregion
	//#region

	static void assertNotEquals(Collection<?> expected, Collection<?> actual) {
		assertNotEquals(expected, actual, null, false);
	}

	static void assertNotEquals(Collection<?> expected, Collection<?> actual, String message) {
		assertNotEquals(expected, actual, message, false);
	}

	static void assertNotEquals(Collection<?> expected, Collection<?> actual, boolean withClass) {
		assertNotEquals(expected, actual, null, withClass);
	}

	static void assertNotEquals(Collection<?> expected, Collection<?> actual, String message, boolean withClass) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}

	//#endregion
	//#region
	
	static void assertNotEquals(Object expected, Object actual) {
		assertNotEquals(expected, actual, null, false);
	}
	static void assertNotEquals(Object expected, Object actual, String message) {
		assertNotEquals(expected, actual, message, false);
	}

	static void assertNotEquals(Object expected, Object actual, boolean withClass) {
		assertNotEquals(expected, actual, null, withClass);
	}

	static void assertNotEquals(Object expected, Object actual, String message, boolean withClass) {
		try {
			Assertions.assertNotEquals(expected, actual, message);
		} catch (Error e) {
			Assertions.assertNotEquals(_toString(expected, withClass), _toString(actual, withClass), message);
			throw e;
		}
	}

	//#endregion

	/******************************/

	static <K, V> String _toString(Map<K, V> map, boolean withClas) {
		return _toString(map, "", withClas);
	}

	static <K, V> String _toString(Map<K, V> map, String prefix, boolean withClass) {
		if (map == null) {
			return "NULL";
		}
		StringBuilder res = new StringBuilder();
		if (withClass) {
			res.append(String.format("(%s) {", map.getClass().getCanonicalName()));
		} else {
			res.append("{");
		}
		map.forEach((k, v)->{
			res.append("\n");
			res.append(prefix);
			res.append(PREFIX);
			res.append(k);
			res.append(": ");
			res.append(_toString(v, prefix + PREFIX, withClass));
		});
		if (!map.isEmpty()) {
			res.append("\n");
			res.append(prefix);
		}
		res.append("}");
		return res.toString();
	}

	static <T> String _toString(Iterable<T> iterable, boolean withClas) {
		return _toString(iterable, "", withClas);
	}

	static <T> String _toString(Iterable<T> iterable, String prefix, boolean withClass) {
		if (iterable == null) {
			return "NULL";
		}
		StringBuilder res = new StringBuilder();
		//res.append(prefix);
		if (withClass) {
			res.append(String.format("(%s) [", iterable.getClass().getCanonicalName()));
		} else {
			res.append("[");
		}
		int i = 0;
		for (T t : iterable) {
			res.append("\n");
			res.append(prefix);
			res.append(PREFIX);
			res.append(_toString(t, prefix + PREFIX, withClass));
			i++;
		}
		if (i > 0) {
			res.append("\n");
			res.append(prefix);
		}
		res.append("]");
		return res.toString();
	}

	static <T> String _toString(T t, boolean withClass) {
		return _toString(t, "", withClass);
	}

	@SuppressWarnings("unchecked")
	static <T> String _toString(T t, String prefix, boolean withClass) {
		if (t == null) {
			return prefix + "NULL";
		}
		if (t instanceof Map map) {
			return _toString(map, prefix, withClass);
		}
		if (t instanceof MapDictionary map) {
			return _toString(map.toMap(), prefix, withClass);
 		}
		if (t instanceof Iterable iterable) {
			return _toString(iterable, prefix, withClass);
		}
		if (t.getClass().isArray()) {
			return _toString(Arrays.asList((T[])t), prefix, withClass);
		}
		if (withClass) {
			return t.toString() + " (" + t.getClass().getCanonicalName() + ")";
		}
		return t.toString();
	}
}
