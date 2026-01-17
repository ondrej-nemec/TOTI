package toti.common.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;

import toti.common.functions.Mapper;

/**
 * Class wrap {@link Map} with implementation of {@link Dictionary}
 * 
 * @author Ondřej Němec
 *
 * @param <K> the type of key
 */
public class MapDictionary<K> implements Dictionary<K>, Iterable<Tuple2<K, Object>> {
	
	private final Map<K, Object> map;
	
	/**
	 * Create new instance as wrapper for {@link HashMap}
	 * 
	 * @return {@link MapDictionary}
	 */
	public static <K> MapDictionary<K> hashMap() {
		return new MapDictionary<>(new HashMap<>());
	}
	/**
	 * Create new instance as wrapper for {@link Properties}
	 * 
	 * @return {@link MapDictionary}
	 */
	public static MapDictionary<Object> properties() {
		return new MapDictionary<>(new Properties());
	}

	/**
	 * 
	 * @param map {@link Map} wrapped map
	 */
	public MapDictionary(Map<K, Object> map) {
		this.map = map;
	}
	
	@Override
	public Object getValue(K name) {
		return map.get(name);
	}
	
	/**
	 * See {@link Map#containsKey(Object)}
	 * 
	 * @param key K identifier
	 * @return true/false
	 */
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	/**
	 * Put new item or override existing
	 * 
	 * @param key K
	 * @param value {@link Object}
	 * @return {@link MapDictionary} self
	 */
	public MapDictionary<K> put(K key, Object value) {
		map.put(key, value);
		return this;
	}

	/**
	 * Put all items from given {@link Map}
	 * 
	 * @param values {@link Map}
	 * @return {@link MapDictionary} self
	 */
	public MapDictionary<K> putAll(Map<K, Object> values) {
		map.putAll(values);
		return this;
	}
	
	/**
	 * Remove item on given key
	 * 
	 * @param key K
	 * @return V removed item or null if identifier is not linked to existing item
	 */
	public Object remove(K key) {
		return map.remove(key);
	}
	
	/**
	 * Returns all keys
	 * 
	 * @return {@link Set} of keys
	 */
	public Set<K> keySet() {
		return map.keySet();
	}
	
	/**
	 * Returns all values
	 * 
	 * @return {@link Collection}
	 */
	public Collection<Object> values() {
		return map.values();
	}
	
	 /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map
     */
	public int size() {
		return map.size();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	/**
	 * Convert {@link MapDictionary} to {@link Map}
	 * 
	 * @return {@link Map}
	 */
	public Map<K, Object> toMap() {
		return map;
	}
	
	/**
	 * Iterate all items
	 * 
	 * @param action {@link BiConsumer} applied on each item, parameters are identifier and item
	 */
	public void forEach2(BiConsumer<K, Object> action) {
		map.forEach(action);
	}
	
	/**
	 * Iterate all items
	 * 
	 * @param <E> the type of expected {@link Exception}
	 * @param action {@link ThrowingBiConsumer} applied on each item, parameters are identifier and item
	 * @throws E expected {@link Exception}
	 */
	public <E extends Throwable> void forEach(ThrowingBiConsumer<K, DictionaryValue, E> action) throws E {
		for (Entry<K, Object> entry : map.entrySet()) {
			action.accept(entry.getKey(), new DictionaryValue(entry.getValue()));
		}
	}
	
	/**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
	public Set<Map.Entry<K, Object>> entrySet() {
		return map.entrySet();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof MapDictionary)  && !(obj instanceof Map)) {
			return false;
		}
		if (obj instanceof Map) {
			Map<?, ?> convertedMap = (Map<?, ?>)obj;
			return map.equals(convertedMap);
		}
		MapDictionary<?> dictionary = (MapDictionary<?>)obj;
		return map.equals(dictionary.map);
	}

	/**
	 * Parse to given class using {@link Mapper}
	 * 
	 * @param <T> expected ype
	 * @param clazz {@link Class}
	 * @return T
	 */
	public <T> T parse(Class<T> clazz) {
		return parse(clazz, null);
	}

	/**
	 * Parse to given class using {@link Mapper}
	 * 
	 * @param <T> expected type
	 * @param clazz {@link Class}
	 * @param key {@link String} serialization key
  	 * @return T
	 */
	public <T> T parse(Class<T> clazz, String key) {
		try {
			return Mapper.get().parse(clazz, map, key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Iterator<Tuple2<K, Object>> iterator() {
		return new Iterator<Tuple2<K, Object>>() {
			private int index = 0;
			private List<K> keys = new ArrayList<>(map.keySet());
			
			@Override
			public boolean hasNext() {
				return index < keys.size();
			}

			@Override
			public Tuple2<K, Object> next() {
				K key = keys.get(index);
				Object value = map.get(key);
				// int i = index;
				index++;
				return new Tuple2<>(key, value);
			}
			
		};
	}
	
}
