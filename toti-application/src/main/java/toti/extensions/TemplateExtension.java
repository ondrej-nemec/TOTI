package toti.extensions;

import java.util.Map;

public interface TemplateExtension {

	String getTemplate(
		String module, String filename, Map<String, Object> params
	) throws Exception;
	
}
