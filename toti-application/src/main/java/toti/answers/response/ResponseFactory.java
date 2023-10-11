package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;

public class ResponseFactory {
	
	private final StatusCode code;
	private final Headers headers;
	
	public ResponseFactory(StatusCode code) {
		this.code = code;
		this.headers = new Headers();
	}
	
	public ResponseFactory addHeader(String name, Object value) {
		this.headers.addHeader(name, value);
		return this;
	}
	
	/**
	 * Very simple file response. Downloaded file name will be last URL parameter. Useful for HTML/CSS/JS resources.
	 * @param fileName specify path to file anywhere on disk
	 * @return
	 */
	public Response getFile(String fileName) {
		return new FileResponse(code, headers, fileName);
	}
	
	/**
	 * File response with headers for downloading.
	 * @param sourceFile file from anywhere on disk
	 * @param fileName specify downloaded file name
	 * @return
	 */
	public Response getFileDownload(String sourceFile, String fileName) {
		return new FileResponse(code, headers, fileName, sourceFile);
	}
	
	/**
	 * Allow create file content dynamically at runtime
	 * @param fileName specify downloaded file name
	 * @param binaryContent
	 * @return
	 */
	public Response getFileDownload(String fileName, byte[] binaryContent) {
		return new FileResponse(code, headers, fileName, binaryContent);
	}
	
	/**
	 * Send given object as JSON. JI Common Mapper is used for stringify.
	 * @param json
	 * @return
	 */
	public Response getJson(Object json) {
		return new JsonResponse(code, headers, json);
	}
	
	/**
	 * Returns given text as response
	 * @param text
	 * @return
	 */
	public Response getText(Object text) {
		return new TextResponse(code, headers, text);
	}
	
	/**
	 * Returns empty response with code
	 * @param code
	 * @return
	 */
	public Response getEmpty(StatusCode code) {
		return new EmptyResponse(code, headers);
	}
	
	/**
	 * Parse file as TOTI template with given parameters
	 * @param fileName path to template files
	 * @param params template paramters
	 * @return
	 */
	public Response getTemplate(String fileName, Map<String, Object> params) {
		return new TemplateResponse(code, headers, fileName, params);
	}
	
	/**
	 * Redirect to given URI. URI must be relative inside app.
	 * @param url - relative path inside application
	 * @return
	 */
	public Response getRedirect(String url) {
		return new RedirectResponse(code, headers, url, false);
	}
	
	/**
	 * Redirect to given URL. Can cause Open redirection vulnerability
	 * @param url
	 * @param allowOutOfAppRedirect - if can redirect out of application
	 * @return
	 */
	public Response getRedirect(String url, boolean allowOutOfAppRedirect) {
		return new RedirectResponse(code, headers, url, allowOutOfAppRedirect);
	}
	
	/**
	 * For websocket response
	 * @param websocket
	 * @param onMessage
	 * @param onError
	 * @return
	 */
	public Response getWebsocket(WebSocket websocket,
			BiConsumer<Boolean, ByteArrayOutputStream> onMessage,
			Consumer<IOException> onError,
			Consumer<String> onClose) {
		return new WebsocketResponse(headers, websocket, onMessage, onError, onClose);
	}

}
