package ji.testing;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import ji.common.functions.Implode;
import ji.common.structures.ThrowingFunction;

public interface TestCase {
	
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
			Assertions.assertEquals(
				expected == null ? "NULL" : Implode.implode("\n", ":", expected),
				actual == null ? "NULL" : Implode.implode("\n", ":", actual),
				message
			);
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
			Assertions.assertEquals(
				expected == null ? "NULL" : Implode.implode("\n", expected),
				actual == null ? "NULL" : Implode.implode("\n", actual),
				message
			);
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
			Assertions.assertEquals(
				expected == null ? "NULL" : expected.toString(),
				actual == null ? "NULL" : expected.toString(),
				message
			);
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
			Assertions.assertNotEquals(
				expected == null ? "NULL" : Implode.implode("\n", ":", expected),
				actual == null ? "NULL" : Implode.implode("\n", ":", actual),
				message
			);
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
			Assertions.assertNotEquals(
				expected == null ? "NULL" : Implode.implode("\n", expected),
				actual == null ? "NULL" : Implode.implode("\n", actual),
				message
			);
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
			Assertions.assertNotEquals(
				expected == null ? "NULL" : expected.toString(),
				actual == null ? "NULL" : expected.toString(),
				message
			);
			throw e;
		}
	}
}
