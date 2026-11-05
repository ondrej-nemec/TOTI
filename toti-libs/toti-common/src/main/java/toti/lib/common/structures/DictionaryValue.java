package toti.lib.common.structures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import toti.lib.common.functions.Mapper;

/**
 * Class is wrapper for any object. The class is able to convert some common types to another
 *  or parse from one type to another. For example can parse numbers or times from string
 * 
 * @author Ondřej Němec
 *
 */
public class DictionaryValue implements NonFixedType {

	private final Object value;
	
	/*private final Function<String, Object> stringMapping = (v)->{
		try {
			Object reader = Class.forName("ji.json.JsonReader").getDeclaredConstructor().newInstance();
			return reader.getClass().getMethod("read", String.class).invoke(reader, v);
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			return v;
		}
	};
	private Function<String, Object> fromStringToListCallback = stringMapping;
	private Function<String, Object> fromStringToMapCallback = stringMapping;*/
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
	/*public DictionaryValue addListCallback(Function<String, Object> fromStringToListCallback) {
		this.fromStringToListCallback = fromStringToListCallback;
		return this;
	}*/
	
	/**
	 * Override default method for parsing {@link Map} from string
	 * 
	 * @param fromStringToMapCallback {@link Function} with string argument and returns {@link Map}
	 * @return {@link DictionaryValue} self
	 */
	/*public DictionaryValue addMapCallback(Function<String, Object> fromStringToMapCallback) {
		this.fromStringToMapCallback = fromStringToMapCallback;
		return this;
	}*/

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

	@Override
	public String toString() {
		return String.format("DV[%s]", value == null ? "NULL" : value.getClass() + ":" + value.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return value == null;
		}
		if (obj instanceof DictionaryValue dv) {
			if (value == null) {
				return dv.value == null;
			}
			return value.equals(dv.value);
		}
		return value.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.value);
		return hash;
	}


}
