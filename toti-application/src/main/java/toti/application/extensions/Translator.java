package toti.application.extensions;

import java.util.Map;

public interface Translator {

	String translate(String key);
	
	String translate(String key, Map<String, Object> params);
	
}
