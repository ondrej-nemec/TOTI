package toti.response;

import java.util.Map;

import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.templating.TemplateFactory;
import translator.Translator;

public interface Response {

	RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset);
	
	void addParam(String name, Object value);
	
	/***********/
	
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
/*	
	@Deprecated
	static Response getHtml(StatusCode code, String fileName, Map<String, Object> params) {
		return new TemplateResponse(code, fileName, params);
	}

	@Deprecated
	static Response getHtml(String fileName, Map<String, Object> params) {
		return new TemplateResponse(StatusCode.OK, fileName, params);
	}
*/	
	static Response getText(String text) {
		return new TextResponse(StatusCode.OK, text);
	}
	
	static Response getText(StatusCode code, String text) {
		return new TextResponse(code, text);
	}
	
	static Response getTemplate(StatusCode code, String fileName, Map<String, Object> params) {
		return new TemplateResponse(code, fileName, params);
	}
	
	static Response getTemplate(String fileName, Map<String, Object> params) {
		return new TemplateResponse(StatusCode.OK, fileName, params);
	}
	
	static Response getRedirect(StatusCode code, String url) {
		return new RedirectResponse(code, url);
	}
	
	static Response getRedirect(String url) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, url);
	}
	/*
	// TODO some param in supported responses
	static Response getPdf(Response response) {
		return new PdfResponse(response);
	}
	*/
}
