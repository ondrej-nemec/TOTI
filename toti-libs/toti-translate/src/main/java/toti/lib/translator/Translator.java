package toti.lib.translator;

import java.util.HashMap;
import java.util.Map;

public class Translator {
	
	/*
	 * nacist soubory
	 * u kazdeho souboru se vybere loader - podle pripony
	 * 
	 * 
	 * messages.cs.properties
	 * common.cs.xml
	 * messages.en.xml
	 * common.en.properties
	 * 
	 */
	
	public String translate(String key) {
		return translate(key, new HashMap<>());
	}

	public String translate(String key, Map<String, Object> variables) {
		// TODO
		/*
		 * split - pokud jde
		 * pokud nepujde, default
		 * taky vybrat podle jazyku - opet def
		 */
		MessagesFile file = null; // TODO
		if (file == null) {
			return key;
		}
		String message = translate(file, key, variables);
		if (message == null) {
			return key;
		}
		return message;
	}

	// TODO test it
	protected String translate(MessagesFile file, String key, Map<String, Object> variables) {
		String message = file.getMessage(key);
		if (message == null) {
			return null;
		}
		return replaceVariables(message, variables);
	}
		
	private String replaceVariables(String value, Map<String, Object> variables) {
		for (String varName : variables.keySet()) {
			Object variable = variables.get(varName);
			value = value.replaceAll("\\%" + varName + "\\%", variable == null ? "" : variable.toString());
		}
		return value;
	}

}
