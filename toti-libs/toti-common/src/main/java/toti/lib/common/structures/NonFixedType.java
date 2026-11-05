package toti.lib.common.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import toti.lib.common.structures.dictionary.Scalar;

public interface NonFixedType extends Scalar {

	/**
	 * Get value as {@link ListDictionary}
	 * <p>
	 * {@link Collection} and array are converted
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} is converted using {@link SortedMap#toList()}
	 * <p>
	 * {@link MapDictionary} and {@link Map} are converted using {@link Map#values()}
	 * 
	 * @return {@link ListDictionary}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default ListDictionary getDictionaryList() {
		return parseValue(ListDictionary.class, (val)->{
			if (val.getClass().isArray()) {
				return new ListDictionary(Arrays.asList((Object[])val));
			}
			if (val instanceof Collection<?>) {
				return new ListDictionary(Collection.class.cast(val));
			}
			if (val instanceof SortedMap<?, ?>) {
				return new ListDictionary(SortedMap.class.cast(val).toList());
			}
			if (val instanceof Map<?, ?>) {
				return new ListDictionary(Map.class.cast(val).values());
			}
			if (val instanceof MapDictionary<?>) {
				return new ListDictionary(MapDictionary.class.cast(val).values());
			}
			return val;
		});
	}
	 
	/**
	 * Get value as {@link MapDictionary}
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} is converted using {@link SortedMap#toMap()}
	 * <p>
	 * {@link Map} is converted
	 * 
	 * @param <T> the type of item
	 * @return {@link MapDictionary}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <T, E> MapDictionary<T> getDictionaryMap() {
		return parseValue(MapDictionary.class, (val)->{
			if (val instanceof Map<?, ?>) {
				return new MapDictionary<>(Map.class.cast(val));
			}
			if (val instanceof SortedMap<?, ?>) {
				return new MapDictionary<>(SortedMap.class.cast(val).toMap());
			}
			if (val instanceof ListDictionary list) {
				Map<Object, Object> map = new HashMap<>();
				list.forEach(item->{
					map.put(item._1().toString(), item._2());
				});
				return new MapDictionary<>(map);
			}
			if (val instanceof Iterable<?> iterable) {
				Map<Object, Object> map = new HashMap<>();
				int i = 0;
				for (Object elem : iterable) {
					map.put((i++) + "", elem);
				}
				return new MapDictionary<>(map);
			}
			return val;
		});
	}
	
	/**
	 * Get value as {@link SortedMap}
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link MapDictionary} and {@link Map} are converted
	 * 
	 * @param <T> the type of item
	 * @return {@link SortedMap}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <T, E> SortedMap<T, E> getSortedMap() {
		return parseValue(SortedMap.class, (val)->{
			if (val instanceof Map<?, ?>) {
				return new SortedMap<T, E>().putAll(Map.class.cast(val));
			}
			if (val instanceof MapDictionary<?>) {
				return new SortedMap<T, E>().putAll(MapDictionary.class.cast(val).toMap());
			}
			return val;
		});
	}

	/**
	 * Get value as {@link List}
	 * <p>
	 * {@link ListDictionary}, array and other {@link Collection} are converted
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} is converted using {@link SortedMap#toList()}
	 * <p>
	 * {@link MapDictionary} and {@link Map} are converted using {@link Map#values()}
	 * 
	 * @param <T> the type of item
	 * @return {@link List}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <T> List<T> getList() {
		return parseValue(List.class, (val)-> {
			if (val.getClass().isArray()) {
				return new ArrayList<>(Arrays.asList((T[])val));
			}
			if (val instanceof ListDictionary) {
				return ListDictionary.class.cast(val).toList();
			}
			if (val instanceof Set<?>) {
				return new LinkedList<>(Set.class.cast(val));
			}
			if (val instanceof SortedMap<?, ?>) {
				return SortedMap.class.cast(val).toList();
			}
			if (val instanceof Map<?, ?>) {
				return Map.class.cast(val).values().stream().collect(Collectors.toList());
			}
			if (val instanceof MapDictionary<?>) {
				return MapDictionary.class.cast(val).values().stream().collect(Collectors.toList());
			}
			return val;
		});
	}
	
	/**
	 * Get value as {@link Set}
	 * <p>
	 * {@link ListDictionary}, array and other {@link Collection} are converted
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} is converted using {@link SortedMap#toList()} and converted from {@link List} to {@link Set}
	 * <p>
	 * {@link MapDictionary} and {@link Map} are converted using {@link Map#keySet()}
	 * 
	 * @param <T> the type of item
	 * @return {@link Set}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <T> Set<T> getSet() {
		return parseValue(Set.class, (val)-> {
			if (val.getClass().isArray()) {
				return new HashSet<>(Arrays.asList((T[])val));
			}
			if (val instanceof ListDictionary || val instanceof List<?>) {
				return new HashSet<>(getList());
			}
			if (val instanceof Map<?, ?>) {
				return new HashSet<>(Map.class.cast(val).keySet());
			}
			if (val instanceof MapDictionary<?>) {
				return new HashSet<>(MapDictionary.class.cast(val).keySet());
			}
			return val;
		});
	}

	/**
	 * Get value as array
	 * <p>
	 * {@link ListDictionary} and {@link Collection} are converted
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} is converted using {@link SortedMap#toList()}
	 * <p>
	 * {@link MapDictionary} and {@link Map} are converted using {@link Map#values()}
	 * 
	 * @param <T> the type of item
	 * @return array
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <T> T[] getArray() {
		Object value = _getValue();
		if (value == null) {
			return null;
		}
		if (value.getClass().isArray()) {
			return (T[])value;
		}
		/*Object val = value;
		if (val instanceof String && fromStringToListCallback != null) {
			val = fromStringToListCallback.apply(val.toString());
		}
		if (val == null) {
			return null;
		}*/
		if (value instanceof ListDictionary) {
			return (T[])getDictionaryList().toArray();
		}
		if (value instanceof List<?>) {
			return (T[])getList().toArray();
		}
		if (value instanceof Set<?>) {
			return (T[])getSet().toArray();
		}
		if (value instanceof Map<?, ?>) {
			return (T[])getMap().values().toArray();
		}
		if (value instanceof MapDictionary<?>) {
			return (T[])getDictionaryMap().values().toArray();
		}
		return (T[])value;
	}

	/**
	 * Get value as {@link Map}
	 * <p>
	 * {@link String} is parsed useing ji.json.JsonReader (if is present in project)
	 * <p>
	 * {@link SortedMap} and {@link MapDictionary} are converted
	 * 
	 * @return {@link Map}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	default <K, V> Map<K, V> getMap() {
		return parseValue(Map.class, (val)-> {
			if (val instanceof MapDictionary<?> map) {
				return map.toMap();
			}
			if (val instanceof SortedMap<?, ?> map) {
				return map.toMap();
			}
			if (val instanceof ListDictionary list) {
				Map<Object, Object> map = new HashMap<>();
				list.forEach(item->{
					map.put(item._1().toString(), item._2());
				});
				return map;
			}
			if (val instanceof Iterable<?> iterable) {
				Map<Object, Object> map = new HashMap<>();
				int i = 0;
				for (Object elem : iterable) {
					map.put((i++) + "", elem);
				}
				return map;
			}
			return val;
		});
	}
	
	/***********/

	private <T> T parseValue(Class<T> clazz, /*Function<String, Object> fromString, */Function<Object, Object> prepare) {
		Object value = _getValue();
		if (value == null) {
			return null;
		}
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		Object val = value;
		/*if (val instanceof String && fromString != null) {
			val = fromString.apply(val.toString());
		}*/
		if (prepare != null && val != null) {
			val = prepare.apply(val);
		}
		if (val == null) {
			return null;
		}
		return clazz.cast(val);
	}
}
