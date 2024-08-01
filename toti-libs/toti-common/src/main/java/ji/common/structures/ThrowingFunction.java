package ji.common.structures;

/**
 * Represents a function that accepts one argument and produces a result.
 * <p>
 * This is {@link FunctionalInterface} whose functional method is {@link #apply(Object)}
 * <p>
 * Instead of result, {@link #apply(Object)} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of the input to the function
 * @param <S> the type of the result of the function
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingFunction<T, S, E extends Throwable> {

	/**
     * Applies this function to the given argument.
	 * 
	 * @param parameter the function argument
	 * @return the function result
	 * @throws E expected {@link Throwable}
	 */
	S apply(T parameter) throws E;
	
}
