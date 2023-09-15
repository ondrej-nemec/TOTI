package toti.answers.request;


import java.util.Optional;

import ji.common.structures.ListDictionary;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;

public class Request {

	private final Headers headers;
	
	private final MapDictionary<String> queryParams;
	private final ListDictionary pathParams;
	private final RequestParameters bodyParams;
	private final byte[] body;
	private final Optional<WebSocket> websocket;
	
	private final MapDictionary<String> data;
	
	public Request(
			Headers headers,
			MapDictionary<String> queryParams,
			RequestParameters bodyParams,
			byte[] body, // TODO optional too
			Optional<WebSocket> websocket) {
		this.queryParams = queryParams;
		this.headers = headers;
		this.bodyParams = bodyParams;
		this.body = body;
		this.data = MapDictionary.hashMap();
		this.pathParams = ListDictionary.linkedList();
		this.websocket = websocket;
	}
	
	public ListDictionary getPathParams() {
		return pathParams;
	}
	
	public MapDictionary<String> getQueryParams() {
		return queryParams;
	}
	
	public RequestParameters getBodyParams() {
		return bodyParams;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public Optional<WebSocket> getWebsocket() {
		return websocket;
	}
	
	/**
	 * Request headers
	 * @return
	 */
	public Headers getHeaders() {
		return headers;
	}
	
	/**
	 * Determite if request is brower link or not
	 * <strong>Result is not 100% sure.</strong>
	 * @return true if request is asychronious
	 */
	public boolean isAsyncRequest() {
		return headers.isAsyncRequest();
	}
	
	public boolean isWebsocketRequest() {
		return websocket.isPresent();
	}
	
	/**
	 * User custom data
	 * Structure is used for transfering information between ResponseBuilder steps
	 * @return {@link MapDictionary}
	 */
	public MapDictionary<String> getData() {
		return data;
	}
	
}
