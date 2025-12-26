package ji.common.structures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface represents structure holiding another object (items). Each item is accesible by identifier.
 * <p>
 * Each item can be converted or parsed using {@link DictionaryValue}
 * 
 * @author Ondřej Němec
 *
 * @param <S> the type of identifier
 * @see DictionaryValue
 */
public interface Dictionary<S> {

	/**
	 * Returns value by given identificator
	 * 
	 * @param key
	 * @return value on given key in original type
	 */
	Object getValue(S key);
	
	void clear();
	
	/**
	 * Returns value by given identificator
	 * 
	 * @param key S identifier
	 * @return {@link DictionaryValue}
	 */
	default DictionaryValue getDictionaryValue(S key) {
		return new DictionaryValue(getValue(key));
	}

	/**
	 * Returns value by given identificator

	 * @param key S identifier
	 * @return not converted value
	 */
	default Object get(S key) {
		return getValue(key);
	}
	
	/**
	 * See {@link DictionaryValue#is(Class)}
	 * 
	 * @param key S identifier
	 * @param clazz
	 * @return true if identifier is contained and item can be converted to given {@link Class}
	 */
	default boolean is(S key, Class<?> clazz) {
		return getDictionaryValue(key).is(clazz);
	}
	
	/**
	 * See {@link DictionaryValue#getBoolean()}
	 * 
	 * @param key S identifier
	 * @return {@link Boolean} or null
	 */
	default Boolean getBoolean(S key) {
		return getDictionaryValue(key).getBoolean();
	}

	/**
	 * See {@link DictionaryValue#getByte()}
	 * 
	 * @param key S identifier
	 * @return {@link Byte} or null
	 */
	default Byte getByte(S key) {
		return getDictionaryValue(key).getByte();
	}

	/**
	 * See {@link DictionaryValue#getInteger()}
	 * 
	 * @param key S identifier
	 * @return {@link Integer} or null
	 */
	default Integer getInteger(S key) {
		return getDictionaryValue(key).getInteger();
	}

	/**
	 * See {@link DictionaryValue#getLong()}
	 * 
	 * @param key S identifier
	 * @return {@link Long} or null
	 */
	default Long getLong(S key) {
		return getDictionaryValue(key).getLong();
	}

	/**
	 * See {@link DictionaryValue#getFloat()}
	 * 
	 * @param key S identifier
	 * @return {@link Float} or null
	 */
	default Float getFloat(S key) {
		return getDictionaryValue(key).getFloat();
	}

	/**
	 * See {@link DictionaryValue#getDouble()}
	 * 
	 * @param key S identifier
	 * @return {@link Double} or null
	 */
	default Double getDouble(S key) {
		return getDictionaryValue(key).getDouble();
	}

	/**
	 * See {@link DictionaryValue#getCharacter()}
	 * 
	 * @param key S identifier
	 * @return {@link Character} or null
	 */
	default Character getCharacter(S key) {
		return getDictionaryValue(key).getCharacter();
	}

	/**
	 * See {@link DictionaryValue#getString()}
	 * 
	 * @param key S identifier
	 * @return {@link String} or null
	 */
	default String getString(S key) {
		return getDictionaryValue(key).getString();
	}

	/**
	 * See {@link DictionaryValue#getTime()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalTime} or null
	 */
	default LocalTime getTime(S key) {
		return getDictionaryValue(key).getTime();
	}

	/**
	 * See {@link DictionaryValue#getDate()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalDate} or null
	 */
	default LocalDate getDate(S key) {
		return getDictionaryValue(key).getDate();
	}

	/**
	 * See {@link DictionaryValue#getDateTime()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalDateTime} or null
	 */
	default LocalDateTime getDateTime(S key) {
		return getDictionaryValue(key).getDateTime();
	}

	/**
	 * See {@link DictionaryValue#getDateTimeZone()}
	 * 
	 * @param key S identifier
	 * @return {@link ZonedDateTime} or null
	 */
	default ZonedDateTime getDateTimeZone(S key) {
		return getDictionaryValue(key).getDateTimeZone();
	}

	/**
	 * See {@link DictionaryValue#getEnum(Class)}
	 * 
	 * @param key S identifier
	 * @return {@link Enum} or null
	 */
	default <E extends Enum<E>> E getEnum(S key, Class<E> enumm) {
		return getDictionaryValue(key).getEnum(enumm);
	}

	/**
	 * See {@link DictionaryValue#getDictionaryList()}
	 * 
	 * @param key S identifier
	 * @return {@link ListDictionary} or null
	 */
	default ListDictionary getDictionaryList(S key) {
		return getDictionaryValue(key).getDictionaryList();
	}

	/**
	 * See {@link DictionaryValue#getDictionaryMap()}
	 * 
	 * @param key S identifier
	 * @return {@link MapDictionary} or null
	 */
	default <T> MapDictionary<T> getDictionaryMap(S key) {
		return getDictionaryValue(key).getDictionaryMap();
	}

	/**
	 * See {@link DictionaryValue#getList()}
	 * 
	 * @param key S identifier
	 * @return {@link List} or null
	 */
	default <T> List<T> getList(S key) {
		return getDictionaryValue(key).getList();
	}

	/**
	 * See {@link DictionaryValue#getMap()}
	 * 
	 * @param key S identifier
	 * @return {@link Map} or null
	 */
	default <K, V> Map<K, V> getMap(S key) {
		return getDictionaryValue(key).getMap();
	}

}
