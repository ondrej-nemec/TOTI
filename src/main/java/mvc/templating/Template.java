package mvc.templating;

import java.util.Map;

public interface Template {

	long getLastModification();
	
	String create(Map<String, Object>variables) throws Exception;

	static String escapreVariable(String variable) {
		return variable
				.replaceAll("\\&", "&amp;") // must be first
				.replaceAll(">", "&gt;")
				.replaceAll("<", "&lt;")
				.replaceAll("\"", "&quot;")
				.replaceAll("'", "&apos;");
	}
}
