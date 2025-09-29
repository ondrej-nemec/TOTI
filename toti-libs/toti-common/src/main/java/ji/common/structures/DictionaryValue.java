package ji.common.structures;

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

import ji.common.functions.Mapper;

/**
 * Class is wrapper for any object. The class is able to convert some common types to another
 *  or parse from one type to another. For example can parse numbers or times from string
 * 
 * @author Ondřej Němec
 *
 */
public class DictionaryValue {

	private final Object value;
	
	private Function<String, Object> stringMapping = (v)->{
		try {
			Object reader = Class.forName("ji.json.JsonReader").getDeclaredConstructor().newInstance();
			return reader.getClass().getMethod("read", String.class).invoke(reader, v);
		} catch (Exception e) {
			return v;
		}
	};
	private Function<String, Object> fromStringToListCallback = stringMapping;
	private Function<String, Object> fromStringToMapCallback = stringMapping;
	private ZoneId zoneId = ZoneId.systemDefault();
	private String onlyKey = null;
	
	/**
	 * Create new instance with given value
	 * 
	 * @param value
	 */
	public DictionaryValue(Object value) {
		this.value = value;
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
	
	/**
	 * Set ZoneId for parsing @link LocalDate}, {@link LocalTime}, {@link LocalDateTime}
	 *  and {@link ZonedDateTime}. Default is <code>ZoneId.systemDefault()</code>
	 * 
	 * @param zoneId {@link ZoneId}
	 * @return {@link DictionaryValue} self
	 */
	public DictionaryValue withZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
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
	
	/**************/
	
	/**
	 * Get value as {@link Boolean}.
	 * <p>
	 * {@link String} is true if is equals (CI): true/on/1
	 * <p>
	 * {@link Number} is true if value is great that 0
	 * 
	 * @return {@link Boolean} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Boolean getBoolean() {
		return parseValue(
			Boolean.class,
			v->v!=null && (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("on") || v.equalsIgnoreCase("1")),
			v->{
				if (v instanceof Number) {
					return Number.class.cast(v).byteValue() > 0;
				}
				return v;
			}
		);
	}

	/**
	 * Get value as {@link Byte}.
	 * 
	 * @return {@link Byte} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Byte getByte() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.byteValue();
		/*return parseValue(
			Byte.class, 
			a->parsePrimitive(a, ()->Byte.parseByte(a)),
			v->Number.class.cast(v).byteValue()
		);*/
	}

	/**
	 * Get value as {@link Short}.
	 * 
	 * @return {@link Short} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Short getShort() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.shortValue();
		/*return parseValue(
			Short.class, 
			a->parsePrimitive(a, ()->Short.parseShort(a)),
			v->Number.class.cast(v).shortValue()
		);*/
	}

	/**
	 * Get value as {@link Integer}.
	 * 
	 * @return {@link Integer} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Integer getInteger() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.intValue();
		/*return parseValue(
			Integer.class, 
			a->parsePrimitive(a, ()->Integer.parseInt(a)), 
			v->Number.class.cast(v).intValue()
		);*/
	}

	/**
	 * Get value as {@link Long}.
	 * 
	 * @return {@link Long} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Long getLong() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.longValue();
		/*return parseValue(
			Long.class,
			a->parsePrimitive(a, ()->Long.parseLong(a)), 
			v->Number.class.cast(v).longValue()
		);*/
	}

	/**
	 * Get value as {@link Float}.
	 * 
	 * @return {@link Float} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Float getFloat() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.floatValue();
		/*return parseValue(
			Float.class, 
			a->parsePrimitive(a, ()->Float.parseFloat(a)), 
			v->Number.class.cast(v).floatValue()
		);*/
	}

	/**
	 * Get value as {@link Double}.
	 * 
	 * @return {@link Double} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Double getDouble() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.doubleValue();
		/*return parseValue(
			Double.class,
			a->parsePrimitive(a, ()->Double.parseDouble(a)),
			v->Number.class.cast(v).doubleValue()
		);*/
	}
	/**
	 * Get value as {@link Number}.
	 * 
	 * @return {@link Number} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Number getNumber() {
        return parseValue(
             Number.class,
             a->{
                 if (a.contains(".")) {
                      return parsePrimitive(a, ()->Double.parseDouble(a));
                 }
                 return parsePrimitive(a, ()->Long.parseLong(a));
             },
             v->Number.class.cast(v)
        );
    }

	/**
	 * Get value as {@link Character}.
	 * 
	 * @return {@link Character} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public Character getCharacter() {
		return parseValue(
			Character.class, 
			a->parsePrimitive(a, ()->a.charAt(0)),
			a->a.toString().charAt(0)
		);
	}

	/**
	 * Get value as {@link String}.
	 * 
	 * @return {@link String} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public String getString() {
		return parseValue(String.class, a->a, a->a.toString());
	}

	/**
	 * Parse value to given {@link Enum}
	 * 
	 * @return {@link Enum} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
     * @throws IllegalArgumentException if the specified enum type has
     *         no constant with the specified name, or the specified
     *         class object does not represent an enum type
	 */
	public <E extends Enum<E>> E getEnum(Class<E> enumm) {
		return parseValue(enumm,a->parsePrimitive(a, ()->E.valueOf(enumm, a)));
	}
	
	private <T> T parsePrimitive(String s, Supplier<T> supplier) {
		if (s == null) {
			return null;
		}
		switch (s.toLowerCase()) {
			case "":
			case "nan":
			case "null":
			case "undefined":
				return null;
			default:
				return supplier.get();
		}
	}
	
	/*****************/

	/**
	 * Get value as {@link LocalTime}.
	 * 
	 * @return {@link LocalTime} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public LocalTime getTime() {
		return getTimestamp(
			LocalTime.class, 
			time->LocalTime.from(time)
		);
	}

	/**
	 * Get value as {@link LocalDate}.
	 * 
	 * @return {@link LocalDate} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public LocalDate getDate() {
		return getTimestamp(
			LocalDate.class, 
			time->LocalDate.from(time)
		);
	}

	/**
	 * Get value as {@link LocalDateTime}.
	 * 
	 * @return {@link LocalDateTime} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public LocalDateTime getDateTime() {
		return getTimestamp(
			LocalDateTime.class, 
			time->LocalDateTime.from(time)
		);
	}

	/**
	 * Get value as {@link ZonedDateTime}.
	 * 
	 * @return {@link ZonedDateTime} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	public ZonedDateTime getDateTimeZone() {
		return getTimestamp(
			ZonedDateTime.class, 
			time->ZonedDateTime.from(time)
		);
	}
	
	private <T> T getTimestamp(Class<T> clazz, Function<TemporalAccessor, T> fromTime) {
		return parseValue(
			clazz,
			(string)->{
				if (string.isEmpty()) {
					return null;
				}
				return getTimestampFromString(string, clazz);
			},
			(object)->{
				if (Long.class.isInstance(object) || long.class.isInstance(object)) {
					long number = Long.class.cast(object);
					if (number > 100000000000L) {
						return fromTime.apply(Instant.ofEpochMilli(number).atZone(zoneId));
					}
					return fromTime.apply(Instant.ofEpochSecond(number).atZone(zoneId));
				}
				if (TemporalAccessor.class.isInstance(object)) {
					return fromTime.apply(createZoneDateTime(object, zoneId));
				}
				if (Date.class.isInstance(object)) {
					return fromTime.apply(Date.class.cast(object).toInstant().atZone(zoneId));
				}
				return getTimestampFromString(object.toString(), clazz);
				// return fromString.apply(object.toString());
			}
		);
	}

	private ZonedDateTime createZoneDateTime(Object object, ZoneId zoneId) {
		if (LocalTime.class.isInstance(object)) {
			return ZonedDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.class.cast(object), zoneId);
		}
		if (LocalDate.class.isInstance(object)) {
			return ZonedDateTime.of(LocalDate.class.cast(object), LocalTime.of(0, 0), zoneId);
		}
		if (LocalDateTime.class.isInstance(object)) {
			return ZonedDateTime.of(LocalDateTime.class.cast(object), zoneId);
		}
		if (ZonedDateTime.class.isInstance(object)) {
			return ZonedDateTime.class.cast(object);
		}
		return null;
	}
	
	private <T> TemporalAccessor getTimestampFromString(String stringValue, Class<T> expected) {
		Map<Class<?>, Function<String, TemporalAccessor>> available = new HashMap<>();
		available.put(LocalTime.class, string->{
			return LocalTime.parse(string, DateTimeFormatter.ISO_TIME);
		});
		available.put(LocalDate.class, string->{
			return LocalDate.parse(string, DateTimeFormatter.ISO_DATE);
		});
		available.put(LocalDateTime.class, (string)->{
			return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
		});
		available.put(ZonedDateTime.class, (string)->{
			return ZonedDateTime.parse(string, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		});
		RuntimeException result = null;
		try {
			return tryTimestamp(available.remove(expected), stringValue);
		} catch (RuntimeException e) {
			result = e;
		}
		for (Function<String, TemporalAccessor> func : available.values()) {
			try {
				return tryTimestamp(func, stringValue);
			} catch (Exception e) {}
		}
		throw result;
	}
	
	private <T> TemporalAccessor tryTimestamp(Function<String, TemporalAccessor> fromString, String string) {
		try {
			return fromString.apply(string);
		} catch (Exception e) {
			return fromString.apply(string.replaceFirst(" ", "T"));
		}
	}

	/************/

	/**
	 * Returns value as {@link List} of strings. If value is string, will be splited using given delimiter
	 * 
	 * @param delimiter {@link String} value for split the string value
	 * @return {@link List}
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	//@SuppressWarnings("unchecked")
	public <T> List<T> getList(String delimiter) {
		if (value == null) {
			return null;
		}
		if (value.toString().isEmpty()) {
			return new LinkedList<>();
		}
		return new DictionaryValue(value.toString().split(delimiter)).getList();
		// return parseValue(List.class, a->Arrays.asList(a.split(delimiter)));
	}
	
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
