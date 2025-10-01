package ji.querybuilder;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.temporal.Temporal;

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
			String string = value.toString().replace("[UTC]", "").replace("T", " ").replace("Z", "+00:00");
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
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			return value.toString();
		}
		if (value instanceof Time) {
			return parseTimeNanos(getString.get());
		}
		if (value instanceof Timestamp || value.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
			return parseTimeNanos(getString.get().replaceFirst(" ", "T").replace(" ", ""));
		}
		return value;
	}
	
	private static String parseTimeNanos(String origin) {
		int dotIndex = origin.lastIndexOf(".");
		int plusIndex = origin.lastIndexOf("+");
		
		String base = "";
		String timeZone = "";
		String nanos = "";
		if (dotIndex < 0 && plusIndex < 0) {
			base = origin;
		} else if (dotIndex < 0 && plusIndex >= 0) {
			base = origin.substring(0, plusIndex);
			timeZone = origin.substring(plusIndex);
		} else if (dotIndex >= 0 && plusIndex < 0) {
			base = origin.substring(0, dotIndex);
			nanos = origin.substring(dotIndex + 1);
		} else {
			base = origin.substring(0, dotIndex);
			timeZone = origin.substring(plusIndex);
			nanos = origin.substring(dotIndex + 1, plusIndex);
		}
		for (int i = nanos.length(); i < 9; i++) {
			nanos += "0";
		}
		return base + "." + nanos + timeZone;
	}
	
}
