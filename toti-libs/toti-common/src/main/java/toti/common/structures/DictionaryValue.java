package toti.common.structures;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import toti.common.functions.Mapper;
import toti.common.structures.dictionary.Scalar;

/**
 * Class is wrapper for any object. The class is able to convert some common types to another
 *  or parse from one type to another. For example can parse numbers or times from string
 * 
 * @author Ondřej Němec
 *
 */
public class DictionaryValue implements Scalar {

	private final Object value;
	
	private final Function<String, Object> stringMapping = (v)->{
		try {
			Object reader = Class.forName("ji.json.JsonReader").getDeclaredConstructor().newInstance();
			return reader.getClass().getMethod("read", String.class).invoke(reader, v);
		} catch (Exception e) {
			return v;
		}
	};
	private Function<String, Object> fromStringToListCallback = stringMapping;
	private Function<String, Object> fromStringToMapCallback = stringMapping;
	private String onlyKey = null;
	
	/**
	 * Create new instance with given value
	 * 
	 * @param value
	 */
	public DictionaryValue(Object value) {
		this.value = value;
	}

    @Override
    public Object _getValue() {
		return value;
    }
	
	/**
	 * Override default method for parsing {@link List} from string
	 * 
	 * @param fromStringToListCallback {@link Function} with string argument and returns {@link List}
	 * @return {@link DictionaryValue} self
	 */
	public DictionaryValue addListCallback(Function<String, Object> fromStringToListCallback) {
		this.fromStringToListCallback = fromStringToListCallback;
		return this;
	}
	
	/**
	 * Override default method for parsing {@link Map} from string
	 * 
	 * @param fromStringToMapCallback {@link Function} with string argument and returns {@link Map}
	 * @return {@link DictionaryValue} self
	 */
	public DictionaryValue addMapCallback(Function<String, Object> fromStringToMapCallback) {
		this.fromStringToMapCallback = fromStringToMapCallback;
		return this;
	}

	/**
	 * Set key for parsing object using {@link Mapper}. Default is null
	 * 
	 * @param onlyKey {@link String} key for parsing
	 * @return {@link DictionaryValue} self
	 * 
	 * @see Mapper
	 */
	public DictionaryValue withOnlyKey(String onlyKey) {
		this.onlyKey = onlyKey;
		return this;
	}
		
	/******/

	/**
	 * Returns real not converted, not parsed value
	 * 
	 * @return {@link Object} original value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Check if value is not null
	 * 
	 * @return true if value is not null
	 */
	public boolean isPresent() {
		return value != null;
	}
	
	/**
	 * Check if value can be converted to given {@link Class}
	 * 
	 * @param clazz {@link Class} tested type
	 * @return true if value can be converted to given {@link Class}
	 */
	public boolean is(Class<?> clazz) {
		try {
			return clazz.isInstance(getValue(clazz));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns value converted to given {@link Class}
	 * 
	 * @param <T> the returned type
	 * @param clazz {@link Class} required type
	 * @return T value converted to given type
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> clazz) {
		if (value == null) {
			return null;
		}
		return (T)getParsedVal(clazz); // with clazz.cast cannot cast object to primitive
	}
	
	@SuppressWarnings("unchecked")
	private <E extends Enum<E>, T> Object getParsedVal(Class<T> clazz) {
		if (clazz.isInstance(value)) {
			return value;
		} else if (clazz.isAssignableFrom(Object.class)) {
			return value;
		} else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
			return getBoolean();
		} else if (clazz.isAssignableFrom(Number.class)) {
            return getNumber();
		} else if (clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class)) {
			return getByte();
		} else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
			return getShort();
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			return getInteger();
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			return getLong();
		} else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
			return getFloat();
		} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
			return getDouble();
		} else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class)) {
			return getCharacter();
		} else if (clazz.isAssignableFrom(String.class)) {
			return value.toString();
		} else if (clazz.isAssignableFrom(Map.class)) {
			return getMap();
		} else if (clazz.isAssignableFrom(List.class)) {
			return getList();
		} else if (clazz.isAssignableFrom(Set.class)) {
			return getSet();
		} else if (clazz.isAssignableFrom(MapDictionary.class)) {
			return getDictionaryMap();
		} else if (clazz.isAssignableFrom(ListDictionary.class)) {
			return getDictionaryList();
		} else if (clazz.isAssignableFrom(SortedMap.class)) {
			return getSortedMap();
		} else if (clazz.isEnum()) {
			return getEnum((Class<E>)clazz);
		} else if (clazz.isAssignableFrom(LocalDate.class)) {
			return getDate();
		} else if (clazz.isAssignableFrom(LocalTime.class)) {
			return getTime();
		} else if (clazz.isAssignableFrom(LocalDateTime.class)) {
			return getDateTime();
		} else if (clazz.isAssignableFrom(ZonedDateTime.class)) {
			return getDateTimeZone();
		} else if (value instanceof MapDictionary || value instanceof Map) { // check very carefully if there is no recursion !
			return getDictionaryMap().parse(clazz, onlyKey);
		} else {
			return value;
		}
	}

	/************/
	
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
	public ListDictionary getDictionaryList() {
		return parseValue(ListDictionary.class, fromStringToListCallback, (value)->{
			if (value.getClass().isArray()) {
				return new ListDictionary(Arrays.asList((Object[])value));
			}
			if (value instanceof Collection<?>) {
				return new ListDictionary(Collection.class.cast(value));
			}
			if (value instanceof SortedMap<?, ?>) {
				return new ListDictionary(SortedMap.class.cast(value).toList());
			}
			if (value instanceof Map<?, ?>) {
				return new ListDictionary(Map.class.cast(value).values());
			}
			if (value instanceof MapDictionary<?>) {
				return new ListDictionary(MapDictionary.class.cast(value).values());
			}
			return value;
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
	public <T, E> MapDictionary<T> getDictionaryMap() {
		return parseValue(MapDictionary.class, fromStringToMapCallback, (value)->{
			if (value instanceof Map<?, ?>) {
				return new MapDictionary<T>(Map.class.cast(value));
			}
			if (value instanceof SortedMap<?, ?>) {
				return new MapDictionary<T>(SortedMap.class.cast(value).toMap());
			}
			return value;
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
	public <T, E> SortedMap<T, E> getSortedMap() {
		return parseValue(SortedMap.class, fromStringToListCallback, (value)->{
			if (value instanceof Map<?, ?>) {
				return new SortedMap<T, E>().putAll(Map.class.cast(value));
			}
			if (value instanceof MapDictionary<?>) {
				return new SortedMap<T, E>().putAll(MapDictionary.class.cast(value).toMap());
			}
			return value;
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
	public <T> List<T> getList() {
		return parseValue(List.class, fromStringToListCallback, (value)-> {
			if (value.getClass().isArray()) {
				return new ArrayList<>(Arrays.asList((T[])value));
			}
			if (value instanceof ListDictionary) {
				return ListDictionary.class.cast(value).toList();
			}
			if (value instanceof Set<?>) {
				return new LinkedList<>(Set.class.cast(value));
			}
			if (value instanceof SortedMap<?, ?>) {
				return SortedMap.class.cast(value).toList();
			}
			if (value instanceof Map<?, ?>) {
				return Map.class.cast(value).values().stream().collect(Collectors.toList());
			}
			if (value instanceof MapDictionary<?>) {
				return MapDictionary.class.cast(value).values().stream().collect(Collectors.toList());
			}
			return value;
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
	public <T> Set<T> getSet() {
		return parseValue(Set.class, fromStringToListCallback, (value)-> {
			if (value.getClass().isArray()) {
				return new HashSet<>(Arrays.asList((T[])value));
			}
			if (value instanceof ListDictionary || value instanceof List<?>) {
				return new HashSet<>(getList());
			}
			if (value instanceof Map<?, ?>) {
				return new HashSet<>(Map.class.cast(value).keySet());
			}
			if (value instanceof MapDictionary<?>) {
				return new HashSet<>(MapDictionary.class.cast(value).keySet());
			}
			return value;
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
	public <T> T[] getArray() {
		if (value == null) {
			return null;
		}
		if (value.getClass().isArray()) {
			return (T[])value;
		}
		Object val = value;
		if (val instanceof String && fromStringToListCallback != null) {
			val = fromStringToListCallback.apply(val.toString());
		}
		if (val == null) {
			return null;
		}
		if (val instanceof ListDictionary) {
			return (T[])getDictionaryList().toArray();
		}
		if (val instanceof List<?>) {
			return (T[])getList().toArray();
		}
		if (val instanceof Set<?>) {
			return (T[])getSet().toArray();
		}
		if (val instanceof Map<?, ?>) {
			return (T[])getMap().values().toArray();
		}
		if (val instanceof MapDictionary<?>) {
			return (T[])getDictionaryMap().values().toArray();
		}
		return (T[])val;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public <T> Iterable<T> getIterable() {
		return parseValue(Set.class, fromStringToListCallback, (value)-> {
			if (value.getClass().isArray()) {
				return Arrays.asList((T[])value);
			}
			if (value instanceof ListDictionary<?>) {
				return ListDictionary.class.cast(value).toList();
			}
			return value;
		});
	}
*/
//	static <T> Iterable<T> toIterable(Object o, Class<T> clazz) {
//		if (o instanceof ListDictionary) {
//			return ListDictionary.class.cast(o).toList();
//		} else if (o.getClass().isArray()) {
//			return java.util.Arrays.asList((T[])o);
//		} else /*if (o16_1 instanceof Iterable<?>)*/ {
//			return (Iterable<T>) o;
//		}
//	}
	
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
	public <K, V> Map<K, V> getMap() {
		return parseValue(Map.class, fromStringToMapCallback, (value)-> {
			if (value instanceof MapDictionary<?>) {
				return MapDictionary.class.cast(value).toMap();
			}
			if (value instanceof SortedMap<?, ?>) {
				return SortedMap.class.cast(value).toMap();
			}
			return value;
		});
	}
	
	/***********/

	private <T> T parseValue(Class<T> clazz, Function<String, Object> fromString, Function<Object, Object> prepare) {
		if (value == null) {
			return null;
		}
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		Object val = value;
		if (val instanceof String && fromString != null && val != null) {
			val = fromString.apply(val.toString());
		}
		if (prepare != null && val != null) {
			val = prepare.apply(val);
		}
		if (val == null) {
			return null;
		}
		return clazz.cast(val);
	}
	
	private <T> T parseValue(Class<T> clazz, Function<String, Object> create) {
		return parseValue(clazz, create, null);
	}
	
	/************/

	@Override
	public String toString() {
		if (value == null) {
			return "NULL";
		}
		return value.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return value.equals(obj);
	}
}
