package toti.response;

import java.util.Map;

import ji.common.functions.FileExtension;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import ji.translator.Translator;

public interface Response {

	RestApiResponse getResponse(
			ResponseHeaders header,
			TemplateFactory templateFactory,
			Translator translator,
			Authorizator authorizator,
			Identity identity,
			MappedUrl current,
			String charset
	);

	default RestApiResponse getResponse(ResponseHeaders header, String charset) {
		return getResponse(header, null, null, null, null, null, charset);
	}
	
	@Deprecated
	void addParam(String name, Object value);
	
	/***********/
	
	static FileResponse getFile(String fileName) {
		return new FileResponse(StatusCode.OK, fileName);
	}

	static FileResponse getFile(StatusCode code, String fileName) {
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
		if (fileName == null) {
			return null;
		}
		String ext = new FileExtension(fileName).getExtension();
		// https://stackoverflow.com/a/48704300
		switch (ext) {
		// TODO more https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
			case "jsp":
			case "html": return "Content-Type: text/html; charset=" + charset;
			case "css": return "Content-Type: text/css; charset=" + charset;
			case "csv": return "Content-Type: text/csv; charset=" + charset;
			case "js": return "Content-Type: text/javascript; charset=" + charset;
			case "txt": return "Content-Type: text/plain; charset=" + charset;
			case "json": return "Content-Type: application/json; charset=" + charset;
			case "ico": return "Content-Type: image/ico";
			case "jpeg":
			case "jpg": return "Content-Type: image/jpeg";
			case "png": return "Content-Type: image/png";
			case "giff": return "Content-Type: image/giff";
			case "gif": return "Content-Type: image/gif";
			
			// TODO add more https://wiki.documentfoundation.org/Faq/General/036
			case "odt": return "Content-Type: application/vnd.oasis.opendocument.text";
			case "ods": return "Content-Type: application/vnd.oasis.opendocument.spreadsheet";
			case "ots": return "Content-Type: application/vnd.oasis.opendocument.spreadsheet-template";
			
			// TODO add microsoft https://filext.com/faq/office_mime_types.html
			
			default: return "Content-Type: application/x-binary"; // application/octet-stream
			//default: return null;
		}
	}
}
