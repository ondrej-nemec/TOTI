package ji.testing;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
}
