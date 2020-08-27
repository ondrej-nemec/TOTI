package mvc.response;

import java.util.List;
import java.util.Map;

import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public interface Response {
	
	RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator);

	static Response getFile(String fileName, String charset) {
		return new FileResponse(StatusCode.OK, fileName, charset);
	}

	static Response getFile(StatusCode code, String fileName, String charset) {
		return new FileResponse(code, fileName, charset);
	}
	
	static Response getJson(StatusCode code, Map<String, Object> json, String charset) {
		return new JsonResponse(code, json, charset);
	}
	
	static Response getJson(Map<String, Object> json, String charset) {
		return new JsonResponse(StatusCode.ACCEPTED, json, charset);
	}
	
	static Response getHtml(StatusCode code, String fileName, Map<String, Object> params, String charset) {
		return new JspResponse(code, fileName, params, charset);
	}
	
	static Response getHtml(String fileName, Map<String, Object> params, String charset) {
		return new JspResponse(StatusCode.OK, fileName, params, charset);
	}
	
	static Response getRedirect(StatusCode code, String url) {
		return new RedirectResponse(code, url);
	}
	
	static Response getRedirect(String url) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, url);
	}
	
}
