package toti.lib.database.querybuilder;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import toti.lib.common.functions.Implode;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ListDictionary;
import toti.lib.common.structures.ThrowingSupplier;

public class Escape {

	private final boolean isZonedDateSupported;

	public Escape() {
		this(true);
	}

	public Escape(boolean isZonedDateSupported) {
		this.isZonedDateSupported = isZonedDateSupported;
	}

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
		} else if (value instanceof LocalDateTime) {
			return escapeLocalDateAndTime(value);
		} else if (value instanceof ZonedDateTime dateTime) {
			return escapeZonedDateAndTime(dateTime);
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

	protected String escapeLocalDateAndTime(Object value) {
		return escapeString(value.toString());
	}

	protected String escapeZonedDateAndTime(ZonedDateTime value) {
		return escapeString(value.toInstant().toString());
	}

	protected String escapeString(Object sql) {
		// maybe??  * @ - _ + . /
		return String.format("'%s'", sql.toString().replaceAll("\\'", "''"));
	}

	/******************************************/

	public Object parseValue(ResultSet rs, int index) throws SQLException {
		return parseValue(()->rs.getObject(index), ()->rs.getString(index));
	}

	public Object parseValue(CallableStatement stmt, int index) throws SQLException {
		return parseValue(()->stmt.getObject(index), ()->stmt.getString(index));
	}
	
	private Object parseValue(
		ThrowingSupplier<Object, SQLException> getObject,
		ThrowingSupplier<String, SQLException> getString
	) throws SQLException {
		Object value = getObject.get();
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			// return value.toString(); // mysql returns another day as raw
			return LocalDate.parse(getString.get());
		}
		if (value instanceof Time) {
			return LocalTime.parse(getString.get());
		}
		if (value instanceof Timestamp) {
			String val = getString.get().replace(" ", "T");
			if (val.contains("+")) {
				return ZonedDateTime.parse(val);
			}
			var dateTime = LocalDateTime.parse(val);
			if (isZonedDateSupported) {
				return dateTime;
			}
			return ZonedDateTime.of(dateTime, ZoneOffset.UTC);
		}
		if (value.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
			return ZonedDateTime.parse(getString.get().replaceFirst(" ", "T").replace(" ", ""));
		}
		return value;
	}
	
}
