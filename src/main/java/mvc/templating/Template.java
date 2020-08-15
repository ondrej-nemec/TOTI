package mvc.templating;

import java.util.Map;

import translator.Translator;

public interface Template {

	long getLastModification();
	
	String create(Map<String, Object>variables, Translator translator) throws Exception;

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
}
