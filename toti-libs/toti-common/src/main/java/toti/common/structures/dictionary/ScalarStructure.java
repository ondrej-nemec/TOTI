package toti.common.structures.dictionary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import toti.common.structures.DictionaryValue;

public interface ScalarStructure<S> {

	/**
	 * Returns value by given identificator
	 * 
	 * @param key
	 * @return value on given key in original type
	 */
	Object getValue(S key);
		
	/**
	 * Returns value by given identificator
	 * 
	 * @param key S identifier
	 * @return {@link DictionaryValue}
	 */
	default Scalar _getValue(S key) {
		return ()->getValue(key);
	}

	/**
	 * See {@link DictionaryValue#getBoolean()}
	 * 
	 * @param key S identifier
	 * @return {@link Boolean} or null
	 */
	default Boolean getBoolean(S key) {
		return _getValue(key).getBoolean();
	}

	/**
	 * See {@link DictionaryValue#getByte()}
	 * 
	 * @param key S identifier
	 * @return {@link Byte} or null
	 */
	default Byte getByte(S key) {
		return _getValue(key).getByte();
	}

	/**
	 * See {@link DictionaryValue#getInteger()}
	 * 
	 * @param key S identifier
	 * @return {@link Integer} or null
	 */
	default Integer getInteger(S key) {
		return _getValue(key).getInteger();
	}

	/**
	 * See {@link DictionaryValue#getLong()}
	 * 
	 * @param key S identifier
	 * @return {@link Long} or null
	 */
	default Long getLong(S key) {
		return _getValue(key).getLong();
	}

	/**
	 * See {@link DictionaryValue#getFloat()}
	 * 
	 * @param key S identifier
	 * @return {@link Float} or null
	 */
	default Float getFloat(S key) {
		return _getValue(key).getFloat();
	}

	/**
	 * See {@link DictionaryValue#getDouble()}
	 * 
	 * @param key S identifier
	 * @return {@link Double} or null
	 */
	default Double getDouble(S key) {
		return _getValue(key).getDouble();
	}

	/**
	 * See {@link DictionaryValue#getCharacter()}
	 * 
	 * @param key S identifier
	 * @return {@link Character} or null
	 */
	default Character getCharacter(S key) {
		return _getValue(key).getCharacter();
	}

	/**
	 * See {@link DictionaryValue#getString()}
	 * 
	 * @param key S identifier
	 * @return {@link String} or null
	 */
	default String getString(S key) {
		return _getValue(key).getString();
	}

	/**
	 * See {@link DictionaryValue#getTime()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalTime} or null
	 */
	default LocalTime getTime(S key) {
		return _getValue(key).getTime();
	}

	/**
	 * See {@link DictionaryValue#getDate()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalDate} or null
	 */
	default LocalDate getDate(S key) {
		return _getValue(key).getDate();
	}

	/**
	 * See {@link DictionaryValue#getDateTime()}
	 * 
	 * @param key S identifier
	 * @return {@link LocalDateTime} or null
	 */
	default LocalDateTime getDateTime(S key) {
		return _getValue(key).getDateTime();
	}

	/**
	 * See {@link DictionaryValue#getDateTimeZone()}
	 * 
	 * @param key S identifier
	 * @return {@link ZonedDateTime} or null
	 */
	default ZonedDateTime getDateTimeZone(S key) {
		return _getValue(key).getDateTimeZone();
	}

	/**
	 * See {@link DictionaryValue#getEnum(Class)}
	 * 
	 * @param key S identifier
	 * @return {@link Enum} or null
	 */
	default <E extends Enum<E>> E getEnum(S key, Class<E> enumm) {
		return _getValue(key).getEnum(enumm);
	}

}
