package toti.lib.database.querybuilder;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.temporal.Temporal;

import toti.lib.common.functions.Implode;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ListDictionary;
import toti.lib.common.structures.ThrowingSupplier;

public class Escape {

	public String escape(Object value) {
		if (value == null) {
			return escapeNull();
		}
		if (value instanceof ListDictionary ld) {
			return Implode.implode(item->escape(item), ",", ld.toList());
		} else if (value instanceof Iterable<?> iterable) {
			return Implode.implode(item->escape(item), ",", iterable);
		} else if (value instanceof DictionaryValue dv) {
			return escape(dv.getValue());
		} else if (value.getClass().isArray()) {
			return Implode.implode(item->escape(item), ",",(Object[])value);
		} else {
			return escapeScalar(value);
		}
	}

	protected String escapeScalar(Object value) {
		Class<?> clazz = value.getClass();
		if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
			return escapeBoolean(value);
		} else if (value instanceof Number) {
			return escapeNumber(value);
		// never happends - always converted to Object
		/*} else if (clazz.isPrimitive() && !clazz.isAssignableFrom(char.class)) {
			return escapeNumericPrimitives(value);*/
		} else if (value instanceof Temporal) {
			return escapeDateAndTime(value);
		} else {
			return escapeString(value);
		}
	}

	protected String escapeNull() {
		return "null";
	}

	protected String escapeBoolean(Object value) {
		return value.toString();
	}

	protected String escapeNumber(Object value) {
		return value.toString();
	}

	protected String escapeNumericPrimitives(Object value) {
		return value.toString();
	}

	protected String escapeDateAndTime(Object value) {
		String string = value.toString()
		.replace("[UTC]", "")
		.replace("[Etc/UTC]", "")
		.replace("T", " ")
		.replace("Z", "+00:00");
		return escapeString(string);
	}

	protected String escapeString(Object sql) {
		// maybe??  * @ - _ + . /
		return String.format("'%s'", sql.toString().replaceAll("\\'", "''"));
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
			// return value.toString(); // mysql returns another day as raw
			return getString.get();
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
		
		String base;
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
		if (dotIndex > 0) {
			for (int i = nanos.length(); i < 9; i++) {
				nanos += "0";
			}
			nanos = "." + nanos;
		}
		return base + nanos + timeZone;
	}
	
}
