package toti.validation;

import java.util.List;
import java.util.Map;

import common.structures.ListDictionary;
import common.structures.MapDictionary;
import json.JsonReader;
import json.JsonStreamException;

public class ParseObject {
	
	public static Object parse(Class<?> clazz, Object object) {
		if (object == null) {
			return null;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			if (object.toString().isEmpty()) { return null; }
			return Integer.parseInt(object + "");
		} else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
			if (object.toString().isEmpty()) { return null; }
			return object.toString().equalsIgnoreCase("true") || object.toString().equalsIgnoreCase("on") || object.toString().equalsIgnoreCase("1");
			// return Boolean.parseBoolean(object + "");
		} else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
			if (object.toString().isEmpty()) { return null; }
			return Short.parseShort(object + "");
		} else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
			if (object.toString().isEmpty()) { return null; }
			return Float.parseFloat(object + "");
		} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
			if (object.toString().isEmpty()) { return null; }
			return Double.parseDouble(object + "");
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			if (object.toString().isEmpty()) { return null; }
			return Long.parseLong(object + "");
		} else if (clazz.isAssignableFrom(Map.class)) {
			if (object instanceof Map) {
				return object;
			}
			if (object instanceof MapDictionary) {
			    return MapDictionary.class.cast(object).toMap();
			}
			if (object.toString().isEmpty()) { return null; }
			try {
				return new JsonReader().read(object.toString());
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		} else if (clazz.isAssignableFrom(List.class)) {
			if (object instanceof List) {
				return object;
			}
			if (object instanceof ListDictionary) {
			    return ListDictionary.class.cast(object).toList();
			}
			if (object.toString().isEmpty()) { return null; }
			try {
				return new JsonReader().read(object.toString());
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		} else if (clazz.isEnum()) {
			if (object instanceof Enum) {
				return object;
			}
			return parseEnum(clazz, object);
		} else {
			return clazz.cast(object);
		}
	}

	@SuppressWarnings("unchecked")
	private static <E extends Enum<E>> Object parseEnum(Class<?> clazz, Object object) {
		return Enum.valueOf((Class<E>)clazz, object.toString());
	}
	
}
