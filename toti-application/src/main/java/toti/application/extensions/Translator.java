package toti.application.extensions;

import java.util.Map;

public interface Translator {

	String translate(String key);
	
	String translate(String key, Map<String, Object> params);

	static Translator createDefault() {
		return new Translator() {
			@Override public String translate(String key) { return key; }
			@Override public String translate(String key, Map<String, Object> params) { return key; }
		};
	}
	
}
