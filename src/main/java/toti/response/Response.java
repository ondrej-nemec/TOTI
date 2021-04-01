package toti.response;

import java.util.Map;

import common.functions.FileExtension;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.templating.TemplateFactory;
import translator.Translator;

public interface Response {

	RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset);
	
	@Deprecated
	void addParam(String name, Object value);
	
	/***********/
	
	static Response getFile(String fileName) {
		return new FileResponse(StatusCode.OK, fileName);
	}

	static Response getFile(StatusCode code, String fileName) {
		return new FileResponse(code, fileName);
	}
	
	static Response getJson(StatusCode code, Object json) {
		return new JsonResponse(code, json);
	}
	
	static Response getJson(Object json) {
		return new JsonResponse(StatusCode.ACCEPTED, json);
	}
	
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
	
	default String getContentType(String fileName, String charset) {
		String ext = new FileExtension(fileName).getExtension();
		// https://stackoverflow.com/a/48704300
		switch (ext) {
			case "jsp":
			case "html": return "Content-Type: text/html; charset=" + charset;
			case "css": return "Content-Type: text/css; charset=" + charset;
			case "js": return "Content-Type: text/javascript; charset=" + charset;
			case "json": return "Content-Type: application/json; charset=" + charset;
			case "ico": return "Content-Type: image/ico";
			case "jpeg":
			case "jpg": return "Content-Type: image/jpeg";
			case "png": return "Content-Type: image/png";
			case "giff": return "Content-Type: image/giff";
			case "gif": return "Content-Type: image/gif";
			case "txt": return "Content-Type: text/plain; charset=" + charset;
			default: return null;
		}
	}
}
