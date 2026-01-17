package toti.common.structures.dictionary;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Scalar {


	Object _getValue();

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
	default Boolean getBoolean() {
		return parseValue(
			Boolean.class,
			v->v.equalsIgnoreCase("true") || v.equalsIgnoreCase("on") || v.equalsIgnoreCase("1"),
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
	default Byte getByte() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.byteValue();
	}

	/**
	 * Get value as {@link Short}.
	 * 
	 * @return {@link Short} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Short getShort() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.shortValue();
	}

	/**
	 * Get value as {@link Integer}.
	 * 
	 * @return {@link Integer} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Integer getInteger() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.intValue();
	}

	/**
	 * Get value as {@link Long}.
	 * 
	 * @return {@link Long} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Long getLong() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.longValue();
	}

	/**
	 * Get value as {@link Float}.
	 * 
	 * @return {@link Float} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Float getFloat() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.floatValue();
	}

	/**
	 * Get value as {@link Double}.
	 * 
	 * @return {@link Double} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Double getDouble() {
		Number num = getNumber();
		if (num == null) {
			return null;
		}
		return num.doubleValue();
	}
	/**
	 * Get value as {@link Number}.
	 * 
	 * @return {@link Number} or null if value is null or value is empty string or value is 'null'(CI string)
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default Number getNumber() {
        return parseValue(
             Number.class,
             a->{
                 if (a.contains(".")) {
                      return parsePrimitive(a, ()->Double.valueOf(a));
                 }
                 return parsePrimitive(a, ()->Long.valueOf(a));
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
	default Character getCharacter() {
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
	default String getString() {
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
	default <E extends Enum<E>> E getEnum(Class<E> enumm) {
		return parseValue(enumm,a->parsePrimitive(a, ()->E.valueOf(enumm, a)), null);
	}
	
	private <T> T parsePrimitive(String s, Supplier<T> supplier) {
		if (s == null) {
			return null;
		}
		return switch (s.toLowerCase()) {
			case "", "nan", "null", "undefined"-> null;
			default -> supplier.get();
		};
	}
	
	/*****************/

	/**
	 * Get value as {@link LocalTime}.
	 * 
	 * @return {@link LocalTime} or null if value is null
	 * @throws ClassCastException if all convert and parse mechanism fails
	 */
	default LocalTime getTime() {
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
	default LocalDate getDate() {
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
	default LocalDateTime getDateTime() {
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
	default ZonedDateTime getDateTimeZone() {
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
				ZoneId zoneId = ZoneId.systemDefault();
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

	private <T> T parseValue(Class<T> clazz, Function<String, Object> fromString, Function<Object, Object> prepare) {
		Object value = _getValue();
		if (value == null) {
			return null;
		}
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		Object val = value;
		if (val instanceof String && fromString != null) {
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
}
