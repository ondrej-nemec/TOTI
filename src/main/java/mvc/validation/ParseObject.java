package mvc.validation;

public class ParseObject {

	public static Object parse(Class<?> clazz, Object object) {
		if (object == null) {
			return null;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			return Integer.parseInt(object + "");
		} else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
			return Boolean.parseBoolean(object + "");
		} else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
			return Short.parseShort(object + "");
		} else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
			return Float.parseFloat(object + "");
		} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
			return Double.parseDouble(object + "");
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			return Long.parseLong(object + "");
		} else {
			return clazz.cast(object);
		}
	}
	
}
