package toti.templating;

import java.util.Map;

public interface TemplateProfiler {
	
	void logGetTemplate(String module, String namespace, String filename, Map<String, Object> variables, int parent, int self);
	
}
