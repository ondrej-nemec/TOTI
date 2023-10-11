package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ji.common.functions.FileExtension;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;
import toti.security.Identity;

public interface Response {

	ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers responseHeaders,
			Identity identity,
			ResponseContainer container,
			String charset
	);

	default ji.socketCommunication.http.structures.Response getResponse(Protocol protocol, Headers responseHeaders, String charset) {
		return getResponse(protocol, responseHeaders, null, null, charset);
	}
	
	/***********/
	/**
	 * Create builder for response
	 * @param code response status code
	 * @return {@link ResponseFactory}
	 */
	static ResponseFactory create(StatusCode code) {
		return new ResponseFactory(code);
	}
	
	/**
	 * For websocket response
	 * @param websocket
	 * @param onMessage
	 * @param onError
	 * @return
	 */
	static Response getWebsocket(WebSocket websocket,
			BiConsumer<Boolean, ByteArrayOutputStream> onMessage,
			Consumer<IOException> onError,
			Consumer<String> onClose) {
		return new WebsocketResponse(new Headers(), websocket, onMessage, onError, onClose);
	}
	
	/***********/
	
	/**
	 * Very simple file response. Downloaded file name will be last URL parameter. Useful for HTML/CSS/JS resources.
	 * @param fileName specify path to file anywhere on disk
	 * @return
	 */
	static Response getFile(String fileName) {
		return new FileResponse(StatusCode.OK, new Headers(), fileName);
	}

	/**
	 * Very simple file response. Downloaded file name will be last URL parameter. Useful for HTML/CSS/JS resources.
	 * @param code Response code
	 * @param fileName specify path to file anywhere on disk
	 * @return
	 */
	static Response getFile(StatusCode code, String fileName) {
		return new FileResponse(code, new Headers(), fileName);
	}
	
	/**
	 * File response with headers for downloading.
	 * @param sourceFile file from anywhere on disk
	 * @param fileName specify downloaded file name
	 * @return
	 */
	static Response getFileDownload(String sourceFile, String fileName) {
		return new FileResponse(StatusCode.OK, new Headers(), fileName, sourceFile);
	}

	/**
	 * File response with headers for downloading.
	 * @param code Response code
	 * @param sourceFile file from anywhere on disk
	 * @param fileName specify downloaded file name
	 * @return
	 */
	static Response getFileDownload(StatusCode code, String sourceFile, String fileName) {
		return new FileResponse(code, new Headers(), fileName, sourceFile);
	}
	
	/**
	 * Allow create file content dynamically at runtime
	 * @param fileName specify downloaded file name
	 * @param binaryContent
	 * @return
	 */
	static Response getFileDownload(String fileName, byte[] binaryContent) {
		return new FileResponse(StatusCode.OK, new Headers(), fileName, binaryContent);
	}

	/**
	 * Allow create file content dynamically at runtime
	 * @param code Response code
	 * @param fileName specify downloaded file name
	 * @param binaryContent
	 * @return
	 */
	static Response getFileDownload(StatusCode code, String fileName, byte[] binaryContent) {
		return new FileResponse(code, new Headers(), fileName, binaryContent);
	}
	
	/**
	 * Send given object as JSON. JI Common Mapper is used for stringify.
	 * @param code
	 * @param json
	 * @return
	 */
	static Response getJson(StatusCode code, Object json) {
		return new JsonResponse(code, new Headers(), json);
	}
	
	/**
	 * Send given object as JSON. JI Common Mapper is used for stringify.
	 * @param json
	 * @return
	 */
	static Response getJson(Object json) {
		return new JsonResponse(StatusCode.OK, new Headers(), json);
	}
	
	/**
	 * Returns given text as response
	 * @param text
	 * @return
	 */
	static Response getText(Object text) {
		return new TextResponse(StatusCode.OK, new Headers(), text);
	}
	
	/**
	 * Returns given text as response
	 * @param code
	 * @param text
	 * @return
	 */
	static Response getText(StatusCode code, Object text) {
		return new TextResponse(code, new Headers(), text);
	}
	
	/**
	 * Returns empty response with code
	 * @param code
	 * @return
	 */
	static Response getEmpty(StatusCode code) {
		return new EmptyResponse(code, new Headers());
	}
	
	/**
	 * Parse file as TOTI template with given parameters
	 * @param code
	 * @param fileName path to template files
	 * @param params template paramters
	 * @return
	 */
	static Response getTemplate(StatusCode code, String fileName, Map<String, Object> params) {
		return new TemplateResponse(code, new Headers(), fileName, params);
	}
	
	/**
	 * Parse file as TOTI template with given parameters
	 * @param fileName path to template files
	 * @param params template paramters
	 * @return
	 */
	static Response getTemplate(String fileName, Map<String, Object> params) {
		return new TemplateResponse(StatusCode.OK, new Headers(), fileName, params);
	}
	
	/**
	 * Redirect to given URI. URI must be relative inside app.
	 * @param code
	 * @param url - relative path inside application
	 * @return
	 */
	static Response getRedirect(StatusCode code, String url) {
		return new RedirectResponse(code, new Headers(), url, false);
	}
	
	/**
	 * Redirect to given URI. URI must be relative inside app.
	 * @param url - relative path inside application
	 * @return
	 */
	static Response getRedirect(String url) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), url, false);
	}
	
	/**
	 * Redirect to given URL. Can cause Open redirection vulnerability
	 * @param code
	 * @param url
	 * @param allowOutOfAppRedirect - if can redirect out of application
	 * @return
	 */
	static Response getRedirect(StatusCode code, String url, boolean allowOutOfAppRedirect) {
		return new RedirectResponse(code, new Headers(), url, allowOutOfAppRedirect);
	}
	
	/**
	 * Redirect to given URL. Can cause Open redirection vulnerability
	 * @param url
	 * @param allowOutOfAppRedirect - if can redirect out of application
	 * @return
	 */
	static Response getRedirect(String url, boolean allowOutOfAppRedirect) {
		return new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), url, allowOutOfAppRedirect);
	}
	
	default void setContentType(String fileName, String charset, Headers responseHeaders) {
		String type = getContentType(fileName, charset);
		if (type != null) {
			responseHeaders.addHeader("Content-Type", type);
		}
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
			case "html": return "text/html; charset=" + charset;
			case "css": return "text/css; charset=" + charset;
			case "csv": return "text/csv; charset=" + charset;
			case "js": return "text/javascript; charset=" + charset;
			case "txt": return "text/plain; charset=" + charset;
			
			case "json": return "application/json; charset=" + charset;
			case "pdf": return "application/pdf";
			
			case "ico": return "image/ico";
			case "jpeg":
			case "jpg": return "image/jpeg";
			case "png": return "image/png";
			case "giff": return "image/giff";
			case "gif": return "image/gif";
			
			// TODO add more https://wiki.documentfoundation.org/Faq/General/036
			case "odt": return "application/vnd.oasis.opendocument.text";
			case "ods": return "application/vnd.oasis.opendocument.spreadsheet";
			case "ots": return "application/vnd.oasis.opendocument.spreadsheet-template";
			
			// TODO add microsoft https://filext.com/faq/office_mime_types.html
			
			default: return "application/x-binary"; // application/octet-stream
			//default: return null;
		}
	}
}
