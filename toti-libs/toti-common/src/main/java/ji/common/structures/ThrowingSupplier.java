package ji.common.structures;

/**
 * Represents a supplier of results.
 * <p>
 * This is {@link FunctionalInterface} whose functional method is {@link #get()}
 * <p>
 * Instead of result, {@link #get()} can throws {@link Throwable}
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of expected child of {@link Throwable}
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

	/**
     * Gets a result.
     *
     * @return a result
	 * @throws E expected {@link Throwable}
	 */
	T get() throws E;
	
}
