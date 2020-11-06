package toti.validation;

import java.util.Map;

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
			return Boolean.parseBoolean(object + "");
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
			if (object.toString().isEmpty()) { return null; }
			try {
				return new JsonReader().read(object.toString());
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		} else {
			return clazz.cast(object);
		}
	}
	
}
