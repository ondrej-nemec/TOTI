package ji.translator;

import java.util.Map;

public interface TransProfiler {
	
	// TODO more detailed - loaded files,...

	void missingParameter(String module, String key, Map<String, Object> variables, String locale);
	
	void missingLocale(String locale);
	
	void loadFile(String locale, String domain, String path, boolean success);
	
}
