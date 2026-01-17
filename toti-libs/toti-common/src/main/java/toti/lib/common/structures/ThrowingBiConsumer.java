package toti.lib.common.structures;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.
 * Unlike most other functional interfaces, {@code ThrowingBiConsumer} is expected
 * to operate via side-effects.
 * <p>
 * This is {@link FunctionalInterface} whose functional method is {@link #accept(Object, Object)}
 * <p>
 * {@link #accept(Object, Object)} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of the first argument to the operation
 * @param <S> the type of the second argument to the operation
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, S, E extends Throwable> {

	/**
	 * Performs this operation on the given arguments.
	 * 
	 * @param first the first input argument
	 * @param second the second input argument
	 * @throws E expected {@link Throwable}
	 */
	void accept(T first, S second) throws E;
	
}
