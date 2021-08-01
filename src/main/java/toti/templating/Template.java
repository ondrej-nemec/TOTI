package toti.templating;

import java.util.LinkedList;
import java.util.Map;

import common.structures.ListDictionary;
import common.structures.MapDictionary;
import toti.security.Authorizator;
import toti.templating.parsing2.TagNode;
import translator.Translator;

public interface Template {

	long getLastModification();
	
	String create(
			TemplateFactory templateFactory, 
			Map<String, Object>variables, 
			Translator translator, 
			Authorizator authorizator) throws Exception;
	
	// TODO will no default
	default String create(
			TemplateFactory templateFactory, 
			Map<String, Object>variables, 
			Translator translator, 
			Authorizator authorizator, LinkedList<TagNode> nodes) throws Exception {
		return create(templateFactory, variables, translator, authorizator);
	}
	
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
		if (o instanceof ListDictionary) {
			return ListDictionary.class.cast(o).toList();
		} else if (o.getClass().isArray()) {
			return java.util.Arrays.asList((T[])o);
		} else /*if (o16_1 instanceof Iterable<?>)*/ {
			return (Iterable<T>) o;
		}
	}
	
	@SuppressWarnings("unchecked")
	static <K, V> Map<K, V> toMap(Object o, Class<K> keyClass, Class<V> valueClass) {
		if (o instanceof MapDictionary) {
			return MapDictionary.class.cast(o).toMap();
		}
		return (Map<K, V>)o;
	}
}
