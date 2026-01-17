package toti.lib.common.functions;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 * Class provide methods for converting any {@link Iterable} to string using given glue
 * 
 * @author Ondřej Němec
 *
 */
public class Implode {
	
	/**
	 * Collapse given arary to string.
	 * Elements will be separated using given glue. Each item is serialized using toString() method.
	 * 
	 * @param <T> type of items
	 * @param glue String separator of items
	 * @param data array of items to collapse
	 * @return String collapsed data
	 */
	@SafeVarargs
	public static <T> String implode(String glue, T... data) {
		return implode((a)->a==null?null:a.toString(), glue, data);
	}

	/**
	 * Collapse given arary to string.
	 * Elements will be separated using given glue. Each item is serialized using given function.
	 * 
	 * @param <T> type of items
	 * @param toString {@link Function} method convert each item to string
	 * @param glue String separator of items
	 * @param data array of items to collapse
	 * @return String collapsed data
	 */
	@SafeVarargs
	public static <T> String implode(Function<T, String> toString, String glue, T... data) {
		return implode(toString, glue, Arrays.asList(data));
	}

	/**
	 * Collapse given {@link Iterable} to string.
	 * Elements will be separated using given glue. Each item is serialized using toString() method.
	 * 
	 * @param <T> type of items
	 * @param glue String separator of items
	 * @param data {@link Iterable} of items to collapse
	 * @return String collapsed data
	 */
	public static <T> String implode(String glue, Iterable<T> data) {
		return implode((a)->a==null?null:a.toString(), glue, data);
	}


	/**
	 * Collapse given {@link Iterable} to string.
	 * Elements will be separated using given glue. Each item is serialized using given function.
	 * 
	 * @param <T> type of items
	 * @param toString {@link Function} method convert each item to string
	 * @param glue String separator of items
	 * @param data {@link Iterable} of items to collapse
	 * @return String collapsed data
	 */
	public static <T> String implode(Function<T, String> toString, String glue, Iterable<T> data) {
		StringBuilder result = new StringBuilder();
		for (T t : data) {
			if (!result.toString().isEmpty()) {
				result.append(glue);
			}
			result.append(toString.apply(t));
		}
		return result.toString();
	}
	
	public static <K, V> String implode(String glue, String delimeter, Map<K, V> map) {
		StringBuilder result = new StringBuilder();
		map.forEach((key, value)->{
			if (!result.toString().isEmpty()) {
				result.append(glue);
			}
			result.append(key.toString());
			result.append(delimeter);
			result.append(value.toString());
		});
		return result.toString();
	}
	
}
