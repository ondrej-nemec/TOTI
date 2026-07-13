package toti.core.extensions;

import java.util.Map;

import toti.core.answers.response.ResponseContainer;

public interface TemplateFactory {

	String getTemplate(String module, String filename, Map<String, Object> params, ResponseContainer container) throws Exception;
	
}
