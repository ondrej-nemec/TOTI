package toti.templating;

import java.util.LinkedList;
import java.util.Map;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ListDictionary;
import ji.common.structures.MapDictionary;
import toti.security.Authorizator;
import toti.templating.parsing.structures.TagNode;
import toti.url.MappedUrl;
import ji.translator.Translator;

public interface Template {

	long getLastModification();
	
	default String create(
			TemplateFactory templateFactory, 
			Map<String, Object>variables, 
			Translator translator, 
			Authorizator authorizator, MappedUrl current) throws Exception {
		return _create(templateFactory, variables, translator, authorizator, new LinkedList<>(), current);
	}
	
	String _create(
			TemplateFactory templateFactory, 
			Map<String, Object>variables, 
			Translator translator, 
			Authorizator authorizator, LinkedList<TagNode> nodes, MappedUrl current) throws Exception;

	//@Deprecated
	static String escapeVariable(Object variable) {
		return escapeHtml(variable);
	}
	
	static boolean check(String string) {
		return string.matches("^([a-zA-Z_]{1})([0-9a-zA-Z_]*)$");
	}
	
	// parameter " &#x22; and ' &#x27;
	// json < to \u003c  >  ;
	
	/**
	 * escape variables in HTML and in parameters except some specials
	 * https://www.w3.org/MarkUp/html-spec/html-spec_13.html
	 */
	static String escapeHtml(Object variable) {
		if (variable == null) {
			return "NULL";
		}
		return variable.toString()
				// html
				.replace("&", "&amp;") // must be first
				.replace(">", "&gt;")
				.replace("<", "&lt;")
				.replace("\"", "&quot;") // &quot;
				.replace("'", "&#x27;") //&apos;
				;
	}
	
	/*
	 * escape parameters executing javascript
	 * source: Nette reengenering
	 */
	static String escapeJsParameter(Object variable) {
		if (variable == null) {
			return "";
		}
		return "&quot;" + variable.toString()
				// html
				.replace("&", "&amp;") // must be first
				.replace(">", "&gt;")
				.replace("<", "&lt;")
				.replace("\"", "\\&#x22;") // &quot;
				.replace("'", "&#x27;") //&apos;
				+ "&quot;"
				;
	}
	
	/*
	 * escape parameters with CSS
	 * source: Nette reengenering
	 */
	static String escapeStyleParameter(Object variable) {
		if (variable == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (char c : variable.toString().toCharArray()) {
			switch (c) {
				case '&': builder.append("\\&amp;"); break;
				case '>': builder.append("\\&gt;"); break;
				case '<': builder.append("\\&lt;"); break;
				case '\"': builder.append("\\&#x22;"); break; // &quot;
				case '\'': builder.append("\\&#x27;"); break; //&apos;
				case '\\': builder.append("\\\\"); break;
				case ':': builder.append("\\:"); break;
				case '/': builder.append("\\/"); break;
				case '(': builder.append("\\("); break;
				case ')': builder.append("\\)"); break;
				case '.': builder.append("\\."); break;
				case '?': builder.append("\\?"); break;
				case '=': builder.append("\\="); break;
				case '+': builder.append("\\+"); break;
				case '#': builder.append("\\#"); break;
				case ';': builder.append("\\;"); break;
				default: builder.append(c);
			}
		}
		return builder.toString();
	}
	
	/*
	 * escape variable with CSS
	 * source: Nette reengenering
	 */
	static String escapeStyle(Object variable) {
		if (variable == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (char c : variable.toString().toCharArray()) {
			switch (c) {
				case '&': builder.append("\\&"); break;
				case '>': builder.append("\\>"); break;
				case '<': builder.append("\\<"); break;
				case '\"': builder.append("\\\""); break; // &quot;
				case '\'': builder.append("\\'"); break; //&apos;
				case '\\': builder.append("\\\\"); break;
				case ':': builder.append("\\:"); break;
				case '/': builder.append("\\/"); break;
				case '(': builder.append("\\("); break;
				case ')': builder.append("\\)"); break;
				case '.': builder.append("\\."); break;
				case '?': builder.append("\\?"); break;
				case '=': builder.append("\\="); break;
				case '+': builder.append("\\+"); break;
				case '#': builder.append("\\#"); break;
				case ';': builder.append("\\;"); break;
				default: builder.append(c);
			}
		}
		return builder.toString();
	}
	
	/*
	 * escape variable printed in JS
	 * source: Nette reengenering
	 */
	static String escapeJavascript(Object variable, Boolean isDoubleQuoted) {
		if (variable == null) {
			return "null";
		}
		if (isDoubleQuoted == null) {
			return '"'
				+ variable.toString()
					.replace("\\", "\\\\")
					.replace("\"", "\\\"")
					.replace("</script>", "")
				+ '"'
				;
		}
		if (isDoubleQuoted) {
			return variable.toString()
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("</script>", "")
				;
		}
		return variable.toString()
			.replace("\\", "\\\\")
			.replace("'", "\\'")
			.replace("</script>", "")
			;
	}
	
	/*
	 * escape variable in url parameters
	 * source: Nette reengenering
	 */
	static String escapeUrl(Object variable) {
		if (variable == null) {
			return "";
		}
		// TODO test and improve
		/*
		http://server.org:80/some/path
		http://server.org/some/path
		https://server.org:80/some/path
		https://server.org/some/path
		https://ser-ver.org/some/path
		https://ser_ver.org/some/path
		/some/path
		some/path
		server.org/some/path
		
		server.org:80/some/path
		javascript:server.org/some/path
		javascript:alert('1')
		 */
		// "${backlink}".replaceAll("&amp;", "&")
		if (variable.toString().matches("^(((http)([s]?)(:\\/\\/)([a-zA-Z0-9\\.\\-_]*)(:([0-9]+))?)?([^:\\n]*))$")) {
			return escapeHtml(variable); // TODO is required
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	static <T> Iterable<T> toIterable(Object o, Class<T> clazz) {
		if (o instanceof ListDictionary) {
			return ListDictionary.class.cast(o).toList();
		}
		if (o.getClass().isArray()) {
			return java.util.Arrays.asList((T[])o);
		}
		if (o instanceof Iterable<?>) {
			return (Iterable<T>) o;
		}
		if (o instanceof DictionaryValue) {
			return DictionaryValue.class.cast(o).getList();
		}
		return new DictionaryValue(o).getList();
	}
	
	@SuppressWarnings("unchecked")
	static <K, V> Map<K, V> toMap(Object o, Class<K> keyClass, Class<V> valueClass) {
		if (o instanceof MapDictionary) {
			return MapDictionary.class.cast(o).toMap();
		}
		if (o instanceof Map) {
			return (Map<K, V>)o;
		}
		if (o instanceof DictionaryValue) {
			return DictionaryValue.class.cast(o).getMap();
		}
		return new DictionaryValue(o).getMap();
	}
	
}
