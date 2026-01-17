package toti.common.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @deprecated will be removed soon
 * Structure like {@link Map} where both key and value are unique
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of key
 * @param <S> the type of value
 */
@Deprecated
public class UniqueMap <T, S> {
	
	private final Map<T, S> first;
	
	private final Map<S, T> second;

	/**
	 * Create new empty instance
	 */
	public UniqueMap() {
		this.first = new HashMap<>();
		this.second = new HashMap<>();
	}
	
	/**
	 * Put next key-value pair
	 * 
	 * @param a key
	 * @param b value
	 */
	public void put(T a, S b) {
		first.put(a, b);
		second.put(b, a);
	}
	
	/**
	 * Get {@code value} using {@code key}
	 * 
	 * @param key
	 * @return value associated with this {@code key}
	 */
	public S getA(T key) {
		return first.get(key);
	}
	
	/**
	 * Get {@code key} using {@code value}
	 * 
	 * @param key
	 * @return key associated with this {@code value}
	 */
	public T getB(S key) {
		return second.get(key);
	}
	
	public void foreachA(BiConsumer<T, S> consumer) {
		first.forEach(consumer);
	}
	
	public void foreachB(BiConsumer<S, T> consumer) {
		second.forEach(consumer);
	}
	
	public Set<T> keySetA() {
		return first.keySet();
	}
	
	public Set<S> keySetB() {
		return second.keySet();
	}
	
}
