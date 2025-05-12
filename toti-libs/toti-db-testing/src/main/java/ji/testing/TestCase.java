package ji.testing;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ji.common.functions.Implode;
import ji.common.structures.ThrowingFunction;

public interface TestCase {
	
	default <T> Consumer<T> consumer(Class<T> clazz, Consumer<T> consumer) {
		return consumer;
	}

	default <T> Supplier<T> supplier(Supplier<T> supplier) {
		return supplier;
	}

	default <T, R> Function<T, R> function(Function<T, R> function) {
		return function;
	}

	default <T, R, E extends Exception> ThrowingFunction<T, R, E> throwingFunction(ThrowingFunction<T, R, E> function) {
		return function;
	}

	default void assertEquals(Map<?, ?> expected, Map<?, ?> actual) {
		assertEquals(null, expected, actual);
	}
	
	default void assertEquals(String message, Map<?, ?> expected, Map<?, ?> actual) {
		try {
			assertEquals(message, expected, actual);
		} catch (Error e) {
			assertEquals(
				message,
				expected == null ? "NULL" : Implode.implode("\n", ":", expected),
				actual == null ? "NULL" : Implode.implode("\n", ":", actual)
			);
			throw e;
		}
	}
	
	default void assertEquals(Collection<?> expected, Collection<?> actual) {
		assertEquals(null, expected, actual);
	}

	default void assertEquals(String message, Collection<?> expected, Collection<?> actual) {
		try {
			assertEquals(message, expected, actual);
		} catch (Error e) {
			assertEquals(
				message,
				expected == null ? "NULL" : Implode.implode("\n", expected),
				actual == null ? "NULL" : Implode.implode("\n", actual)
			);
			throw e;
		}
	}

	default void assertEquals(Object expected, Object actual) {
		assertEquals(null, expected, actual);
	}

	default void assertEquals(String message, Object expected, Object actual) {
		try {
			assertEquals(message, expected, actual);
		} catch (Error e) {
			assertEquals(
				message,
				expected == null ? "NULL" : expected.toString(),
				actual == null ? "NULL" : expected.toString()
			);
			throw e;
		}
	}
}
