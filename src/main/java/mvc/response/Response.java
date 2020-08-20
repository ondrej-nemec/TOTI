package mvc.response;

import java.util.List;
import java.util.Map;

import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public interface Response {
	
	RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator);

	static FileResponse getFileResponse(String fileName) {
		return new FileResponse(StatusCode.OK, fileName);
	}

	static FileResponse getFileResponse(StatusCode code, String fileName) {
		return new FileResponse(code, fileName);
	}
	
	static JsonResponse getJsonResponse(StatusCode code, Map<String, Object> json) {
		return new JsonResponse(code, json);
	}
	
	static JsonResponse getJsonResponse(Map<String, Object> json) {
		return new JsonResponse(StatusCode.ACCEPTED, json);
	}
	
	static HtmlResponse getHtmlResponse(StatusCode code, String fileName, Map<String, Object> params) {
		return new HtmlResponse(code, fileName, params);
	}
	
	static HtmlResponse getHtmlResponse(String fileName, Map<String, Object> params) {
		return new HtmlResponse(StatusCode.OK, fileName, params);
	}
	
	static RedirectResponse getRedirect(StatusCode code, String url) {
		return new RedirectResponse(code, url);
	}
	
	static RedirectResponse getRedirect(String url) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, url);
	}
	
}
