package toti.lib.common.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class serves as {@link Map} builder
 * 
 * @author Ondřej Němec
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class MapInit<K, V> {
	
	/**
	 * Create new {@link MapInit} with {@link String} key and {@link Object} value
	 * 
	 * @return {@link MapInit} new instance
	 */
	public static MapInit<String, Object> create() {
		return new MapInit<>();
	}

	public static <K, V> MapInit<K, V> create(Class<K> k, Class<V> v) {
		return new MapInit<>();
	}
	
	private final Map<K, V> map;
	
	/**
	 * Create new instance with empty {@link Map}
	 */
	public MapInit() {
		this(new HashMap<>());
	}
	
	/**
	 * Create new instance with first item
	 * 
	 * @param k key
	 * @param v value
	 */
	public MapInit(K k, V v) {
		this.map = new HashMap<>();
		map.put(k, v);
	}
	
	/**
	 * Create new instance from given {@link Map}
	 * 
	 * @param map {@link Map}
	 */
	public MapInit(Map<K, V> map) {
		this.map = map;
	}
	
	/**
	 * Add new pair to map
	 * 
	 * @param key
	 * @param value
	 * @return {@link MapInit} self
	 */
	public MapInit<K, V> append(K key, V value) {
		map.put(key, value);
		return this;
	}
	
	/**
	 * Convenrt to {@link Map}
	 * 
	 * @return {@link Map}
	 */
	public Map<K, V> toMap() {
		return map;
	}
	
	/**
	 * Convert to {@link SortedMap}
	 * 
	 * @return {@link SortedMap}
	 */
	public SortedMap<K, V> toSortedMap() {
		return new SortedMap<K, V>().putAll(map);
	}
	
	/**
	 * Convert to {@link Properties}
	 * 
	 * @return {@link Properties}
	 */
	public Properties toProperties() {
		Properties prop = new Properties();
		prop.putAll(map);
		return prop;
	}
	
	/**
	 * Convert to {@link MapDictionary}
	 * 
	 * @return {@link MapDictionary}
	*/
	public MapDictionary<K> toDictionaryMap() {
		return new DictionaryValue(map).getDictionaryMap();
	}
}
