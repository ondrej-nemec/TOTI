package toti.common.structures;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code ThrowingConsumer} is expected
 * to operate via side-effects.
 * <p>
 * This is {@link FunctionalInterface} whose functional method is {@link #accept(Object)}
 * <p>
 * {@link #accept(Object)} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of the input to the operation
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

	/**
	 * Performs this operation on the given argument.
	 * 
	 * @param param the input argument
	 * @throws E expected {@link Throwable}
	 */
	void accept(T param) throws E;
	
}
