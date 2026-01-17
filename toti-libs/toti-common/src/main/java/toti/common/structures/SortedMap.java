package toti.common.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Class represent structure where items are sorted like in {@link Collection} but are accesible using keys like in {@link Map}
 * 
 * @author Ondřej Němec
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class SortedMap<K, V> implements Dictionary<K>, Iterable<Tuple3<K, V, Integer>> {

	private final List<K> order;
	private final Map<K, V> data;
	
	/**
	 * Create new empty instance
	 */
	public SortedMap() {
		this.order = new LinkedList<>();
		this.data = new HashMap<>();
	}
	
	/**
	 * Put all source key-values pairs
	 * 
	 * @param source {@link Map}
	 * @return {@link SortedMap} self
	 */
	public SortedMap<K, V> putAll(Map<K, V> source) {
		source.forEach((k, v)->{
			put(k, v);
		});
		return this;
	}
	
	/**
	 * Add as last item
	 * 
	 * Difference between {@link SortedMap#put(Object, Object)} and {@link SortedMap#append(Object, Object)}
	 * is that {@link SortedMap#put(Object, Object)} returns original value associated with {@code key}
	 * and {@link SortedMap#append(Object, Object)} returns self
	 * 
	 * @param key
	 * @param value
	 * @return the previous value associated with {@code key}, or
	 *		 {@code null} if there was no mapping for {@code key}.
	 */
	public V put(K key, V value) {
		if (!data.containsKey(key)) {
			order.add(key);
		}
		return data.put(key, value);
	}
	
	/**
	 * Check if is empty
	 * 
	 * @return {@link Boolean} true if is empty, false otherwise
	 */
	public boolean isEmpty() {
		return order.isEmpty();
	}
	
	public int size() {
		return order.size();
	}
	
	/**
	 * Add as last item
	 * 
	 * Difference between {@link SortedMap#put(Object, Object)} and {@link SortedMap#append(Object, Object)}
	 * is that {@link SortedMap#put(Object, Object)} returns original value associated with {@code key}
	 * and {@link SortedMap#append(Object, Object)} returns self
	 * 
	 * @param key
	 * @param value
	 * @return {@link SortedMap} self
	 */
	public SortedMap<K, V> append(K key, V value) {
		put(key, value);
		return this;
	}
	
	@Override
	public V getValue(K key) {
		return data.get(key);
	}
	
	@Override
	public V get(K key) {
		return getValue(key);
	}
	
	public V getByIndex(int index) {
		if (order.size() > index) {
			return get(order.get(index));
		}
		return null;
	}
	
	public K getKeyByIndex(int index) {
		if (order.size() > index) {
			return order.get(index);
		}
		return null;
	}
	
	public V getFirst() {
		K key = getFirstKey();
		if (key == null) {
			return null;
		}
		return get(key);
	}

	public K getFirstKey() {
		if (!order.isEmpty()) {
			return this.order.get(0);
		}
		return null;
	}

	public V getLast() {
		K key = getLastKey();
		if (key == null) {
			return null;
		}
		return get(key);
	}

	public K getLastKey() {
		if (!order.isEmpty()) {
			return order.get(order.size() - 1);
		}
		return null;
	}

	@Override
	public void clear() {
		data.clear();
		order.clear();
	}
	
	/**
	 * Returns {@code true} if this map contains a mapping for the specified key.
	 *
	 * @param key key whose presence is to be tested
	 * @return {@code true} if this map contains a mapping for the specified
	 *		 key
	 * @throws ClassCastException if the key is of an inappropriate type for
	 *		 this map
	 * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified key is null and this map
	 *		 does not permit null keys
	 * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	public boolean containsKey(K key) {
		return data.containsKey(key);
	}
	
	 /**
	 * Removes the mapping for a key if it is present
	 * (optional operation).
	 *
	 * <p>Returns the value to which {@link SortedMap} previously associated the key,
	 * or {@code null} if the {@link SortedMap} contained no mapping for the key.
	 *
	 * <p>If this {@link SortedMap} permits null values, then a return value of
	 * {@code null} does not <i>necessarily</i> indicate that the {@link SortedMap}
	 * contained no mapping for the key; it's also possible that the {@link SortedMap}
	 * explicitly mapped the key to {@code null}.
	 *
	 * <p>The {@link SortedMap} will not contain a mapping for the specified key once the
	 * call returns.
	 *
	 * @param key key whose mapping is to be removed from the {@link SortedMap}
	 * @return the previous value associated with {@code key}, or
	 *		 {@code null} if there was no mapping for {@code key}.
	 * @throws UnsupportedOperationException if the {@code remove} operation
	 *		 is not supported by this map
	 * @throws ClassCastException if the key is of an inappropriate type for
	 *		 this map
	 * @throws NullPointerException if the specified key is null and this
	 *		 {@link SortedMap} does not permit null keys
	 */
	public V remove(K key) {
		V remove = data.remove(key);
		if (order.contains(key)) {
			order.remove(key);
		}
		return remove;
	}
	
	/**
	 * Iterate over all items
	 * 
	 * @param callback {@link BiConsumer} action that will be applied on each item. First parameter
	 * is <code>key</code> and the second is <code>value</code>
	 */
	public void forEach(BiConsumer<K, V> callback) {
		order.forEach((key)->{
			callback.accept(key, data.get(key));
		});
	}

	/**
	 * Iterate over all items
	 * 
	 * @param callback {@link ThrowingBiConsumer} action that will be applied on each item. First parameter
	 * is <code>key</code> and the second is <code>value</code>
	 * @param clazz {@link Class} class of expected {@link Exception}
	 * @throws E expected {@link Exception} specified by <code>clazz</code>
	 */
	public <E extends Throwable> void forEach(ThrowingBiConsumer<K, V, E> callback, Class<E> clazz) throws E {
		for (K key : order) {
			 callback.accept(key, data.get(key));
		}
	}

	/**
	 * Iterate over all items
	 * 
	 * @param callback {@link ThrowingTriConsumer} action that will be applied on each item. The first parameter
	 * is <code>index</code>, the second is <code>key</code> and the third is <code>value</code>
	 * @throws E expected {@link Exception} specified by <code>clazz</code>
	 */
	public <E extends Throwable> void forEach(ThrowingTriConsumer<Integer, K, V, E> callback) throws E {
		for (int i = 0; i < order.size(); i++) {
			 K key = order.get(i);
			 callback.accept(i, key, data.get(key));
		}
	}
	
	/**
	 * Convert to {@link Map}
	 * 
	 * @return {@link Map}
	 */
	public Map<K, V> toMap() {
		return data;
	}
	
	/**
	 * Convert to {@link List}
	 * 
	 * @return {@link List} of values
	 */
	public List<V> toList() {
		List<V> list = new LinkedList<>();
		forEach((k, v)->{
			list.add(v);
		});
		return list;
	}
	
	public List<K> getKeys() {
		return order;
	}

	@Override
	public String toString() {
		if (order.isEmpty()) {
			return "[]";
		}
		StringBuilder result = new StringBuilder();
		forEach((k, v)->{
			result.append(String.format(",{\"%s\": \"%s\"}", k, v));
		});
		return "[" + result.toString().substring(1) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SortedMap<?, ?> other = (SortedMap<?, ?>) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (order == null) {
			if (other.order != null) {
				return false;
			}
		} else if (!order.equals(other.order)) {
			return false;
		}
		return true;
	}

	@Override
	public Iterator<Tuple3<K, V, Integer>> iterator() {
		return new Iterator<Tuple3<K, V, Integer>>() {
			private int index = 0;
			@Override
			public boolean hasNext() {
				return index < order.size();
			}

			@Override
			public Tuple3<K, V, Integer> next() {
				K k = order.get(index);
				V v = data.get(k);
				int i = index;
				index++;
				return new Tuple3<>(k, v, i);
			}
			
		};
	}
	
}
