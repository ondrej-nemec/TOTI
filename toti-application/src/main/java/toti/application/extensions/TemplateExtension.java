package toti.application.extensions;

import java.util.Map;

import toti.application.answers.response.ResponseContainer;

public interface TemplateExtension {

	String getTemplate(
		String module, String filename, Map<String, Object> params,
		ResponseContainer container
	) throws Exception;
	
}
