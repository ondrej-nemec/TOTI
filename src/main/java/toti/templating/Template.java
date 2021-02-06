package toti.templating;

import java.util.Map;

import translator.Translator;

public interface Template {

	long getLastModification();
	
	String create(TemplateFactory templateFactory, Map<String, Object>variables, Translator translator) throws Exception;
	
	static String escapeVariable(Object variable) {
		if (variable == null) {
			return "NULL";
		}
		return variable.toString()
				.replaceAll("\\&", "&amp;") // must be first
				.replaceAll(">", "&gt;")
				.replaceAll("<", "&lt;")
				.replaceAll("\"", "&quot;")
				.replaceAll("'", "&apos;");
	}
	
	@SuppressWarnings("unchecked")
	static <T> Iterable<T> toIterable(Object o, Class<T> clazz) {
		if (o.getClass().isArray()) {
			return java.util.Arrays.asList((T[])o);
		} else /*if (o16_1 instanceof Iterable<?>)*/ {
			return (Iterable<T>) o;
		}
	}
	
	@SuppressWarnings("unchecked")
	static <K, V> Map<K, V> toMap(Object o, Class<K> keyClass, Class<V> valueClass) {
		return (Map<K, V>)o;
	}
}
