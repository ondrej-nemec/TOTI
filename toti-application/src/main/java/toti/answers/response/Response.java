package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ji.common.functions.FileExtension;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.WebSocket;
import toti.answers.Headers;
import toti.answers.request.Identity;

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

	static ResponseFactory OK() {
		return create(StatusCode.OK);
	}

	static ResponseFactory FORBIDDEN() {
		return create(StatusCode.FORBIDDEN);
	}

	static ResponseFactory TEMPORARY_REDIRECT() {
		return create(StatusCode.TEMPORARY_REDIRECT);
	}

	static ResponseFactory NOT_FOUND() {
		return create(StatusCode.NOT_FOUND);
	}
	
	static ResponseFactory INTERNAL_SERVER_ERROR() {
		return create(StatusCode.INTERNAL_SERVER_ERROR);
	}

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
	
	/***************************/
	
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
