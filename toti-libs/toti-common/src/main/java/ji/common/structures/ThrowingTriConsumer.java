package ji.common.structures;

/**
 * Represents an operation that accepts three inputs argument and returns no
 * result. Unlike most other functional interfaces, {@code ThrowingTriConsumer} is expected
 * to operate via side-effects.
 * <p>
 * This is {@link FunctionalInterface} whose functional method is {@link #accept(Object, Object, Object)}
 * <p>
 * {@link #accept(Object, Object, Object)} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of the first input to the operation
 * @param <S> the type of the second input to the operation
 * @param <R> the type of the third input to the operation
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingTriConsumer<T, S, R, E extends Throwable> {

	/**
	 * Performs this operation on the given argument.
	 * 
	 * @param param1 the first input argument
	 * @param param2 the second input argument
	 * @param param3 the third input argument
	 * @throws E expected {@link Throwable}
	 */
	void accept(T param1, S param2, R param3) throws E;
	
}
