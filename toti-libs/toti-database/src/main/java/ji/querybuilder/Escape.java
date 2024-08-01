package ji.querybuilder;

import java.time.temporal.Temporal;

import ji.common.functions.Implode;
import ji.common.structures.DictionaryValue;
import ji.common.structures.ListDictionary;

public class Escape {

	public static String escape(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Iterable<?>) {
			Iterable<?> iterable = Iterable.class.cast(value);
			return Implode.implode(item->escapeScalar(item), ",", iterable);
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
			return escapeString(value.toString().replace("T", " "));
		} else {
			return escapeString(value.toString());
		}
	}

	private static String escapeString(String sql) {
		// maybe??  * @ - _ + . /
		return String.format("'%s'", sql.replaceAll("\\'", "''"));
	}
	
}
