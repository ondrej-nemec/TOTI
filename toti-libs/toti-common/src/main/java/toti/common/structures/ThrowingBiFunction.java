package toti.common.structures;

/**
 * Represents a function that accepts two arguments and produces a result.
 * 
 * This is {@link FunctionalInterface} whose functional method is {@link #apply(Object, Object)}
 * <p>
 * Instead of result, {@link #apply(Object, Object)} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the second argument to the function
 * @param <S> the type of the result of the function
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, R, S, E extends Throwable> {

	/**
     * Applies this function to the given arguments.	 * 
	 * 
	 * @param first the first function argument
	 * @param second the second function argument
	 * @return the function result
	 * @throws E expected {@link Throwable}
	 */
	S apply(T first, R second) throws E;
	
}
