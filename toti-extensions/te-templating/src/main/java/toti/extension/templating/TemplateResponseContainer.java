package toti.extension.templating;

import java.util.Map;

import toti.answers.response.ResponseContainer;
import toti.templating.TemplateContainer;

public class TemplateResponseContainer implements TemplateContainer {

	private final ResponseContainer container;

	public TemplateResponseContainer(ResponseContainer container) {
		this.container = container;
	}
	
	public String translate(String value) {
		return container.getTranslator().translate(value);
	}
	
	public String translate(String value, Map<String, Object> params) {
		return container.getTranslator().translate(value, params);
	}
	
	public String createLink(String link) {
		return container.getLink().create(link);
	}
	
	public String createLink(String controller, String method,
		Map<String, Object> queryParams, Object...pathParams) {
		return container.getLink().create(controller, method, queryParams, pathParams);
	}
	
}
