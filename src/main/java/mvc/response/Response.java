package mvc.response;

import java.util.List;
import java.util.Map;

import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public interface Response {
	
	RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator, String charset);

	static Response getFile(String fileName) {
		return new FileResponse(StatusCode.OK, fileName);
	}

	static Response getFile(StatusCode code, String fileName) {
		return new FileResponse(code, fileName);
	}
	
	static Response getJson(StatusCode code, Map<String, Object> json) {
		return new JsonResponse(code, json);
	}
	
	static Response getJson(Map<String, Object> json) {
		return new JsonResponse(StatusCode.ACCEPTED, json);
	}
	
	static Response getHtml(StatusCode code, String fileName, Map<String, Object> params) {
		return new JspResponse(code, fileName, params);
	}
	
	static Response getHtml(String fileName, Map<String, Object> params) {
		return new JspResponse(StatusCode.OK, fileName, params);
	}
	
	static Response getRedirect(StatusCode code, String url) {
		return new RedirectResponse(code, url);
	}
	
	static Response getRedirect(String url) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, url);
	}
	
}
