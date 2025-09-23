package ji.querybuilder;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import org.apache.commons.lang3.StringUtils;

import ji.common.functions.Implode;
import ji.common.structures.DictionaryValue;
import ji.common.structures.ListDictionary;
import ji.common.structures.ThrowingSupplier;

public class Escape {

	public static String escape(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Iterable<?>) {
			Iterable<?> iterable = Iterable.class.cast(value);
			return Implode.implode(item->escapeScalar(item), ",", iterable) ;
		} else if (value instanceof ListDictionary) {
			ListDictionary iterable = ListDictionary.class.cast(value);
			return Implode.implode(item->escapeScalar(item), ",", iterable.toList());
		} else if (value instanceof DictionaryValue) {
			return escapeScalar(DictionaryValue.class.cast(value).getValue());
		} else {
			return escapeScalar(value);
		}
	}

	private static String escapeScalar(Object value) {
		Class<?> clazz = value.getClass();
		if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
			//  value ? "1" : "0"
			return value.toString();
		} else if (value instanceof Number) {
			return value.toString();
		} else if (clazz.isPrimitive() && !(clazz.isAssignableFrom(byte.class) || clazz.isAssignableFrom(char.class))) {
			return value.toString();
		} else if (value instanceof Temporal) {
			String string = value.toString().replace("[UTC]", " ").replace("T", " ");
			return escapeString(string);
		} else {
			return escapeString(value.toString());
		}
	}

	private static String escapeString(String sql) {
		// maybe??  * @ - _ + . /
		return String.format("'%s'", sql.replaceAll("\\'", "''"));
	}

	/******************************************/

	public static Object parseValue(ResultSet rs, int index) throws SQLException {
		return parseValue(()->rs.getObject(index), ()->rs.getString(index));
	}

	public static Object parseValue(CallableStatement stmt, int index) throws SQLException {
		return parseValue(()->stmt.getObject(index), ()->stmt.getString(index));
	}
	
	private static Object parseValue(
		ThrowingSupplier<Object, SQLException> getObject,
		ThrowingSupplier<String, SQLException> getString
	) throws SQLException {
		Object value = getObject.get();
		if (value instanceof Date) {
			return LocalDate.parse(value.toString());
		}
		if (value instanceof Time) {
			return LocalTime.parse(getString.get());
		}
		if (value instanceof Timestamp || value.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
			String text = getString.get();
			text = text.replaceFirst(" ", "T").replaceFirst(" ", "");
			if (text.contains("+") || StringUtils.countMatches(text, "-") > 3 || text.contains("Z")) {
				if (StringUtils.countMatches(text, ":") == 2) {
					text += ":00";
				}
				return ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);
			} else {
				return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
			}
		}
		return value;
	}
	
}
