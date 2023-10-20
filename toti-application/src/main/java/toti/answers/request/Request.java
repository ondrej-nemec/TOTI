package toti.answers.request;


import java.util.Arrays;
import java.util.Optional;

import ji.common.structures.ListDictionary;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.WebSocket;
import toti.answers.Headers;

public class Request {

	private final Headers headers;
	
	private final HttpMethod method;
	private final MapDictionary<String> queryParams;
	private final ListDictionary pathParams;
	private final RequestParameters bodyParams;
	private final byte[] body;
	private final Optional<WebSocket> websocket;
	
	private final MapDictionary<String> data;
	
	public static Request fromRequest(ji.socketCommunication.http.structures.Request request, Headers requestHeaders) {
		return fromRequest(request, requestHeaders, Optional.empty());
	}
	
	public static Request fromRequest(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			Optional<WebSocket> websocket
		) {
		return new Request(
			request.getMethod(),
			requestHeaders,
			request.getQueryParameters(),
			request.getBodyInParameters(),
			request.getBody(),
			websocket
		);
	}
	
	public Request(
			HttpMethod method,
			Headers headers,
			MapDictionary<String> queryParams,
			RequestParameters bodyParams,
			byte[] body, // TODO optional too
			Optional<WebSocket> websocket) {
		this.method = method;
		this.queryParams = queryParams;
		this.headers = headers;
		this.bodyParams = bodyParams;
		this.body = body;
		this.data = MapDictionary.hashMap();
		this.pathParams = ListDictionary.linkedList();
		this.websocket = websocket;
	}
	
	public HttpMethod getMethod() {
		return method;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(body);
		result = prime * result + ((bodyParams == null) ? 0 : bodyParams.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((pathParams == null) ? 0 : pathParams.hashCode());
		result = prime * result + ((queryParams == null) ? 0 : queryParams.hashCode());
		result = prime * result + ((websocket == null) ? 0 : websocket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Request other = (Request) obj;
		if (!Arrays.equals(body, other.body)) {
			return false;
		}
		if (bodyParams == null) {
			if (other.bodyParams != null) {
				return false;
			}
		} else if (!bodyParams.equals(other.bodyParams)) {
			return false;
		}
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (pathParams == null) {
			if (other.pathParams != null) {
				return false;
			}
		} else if (!pathParams.equals(other.pathParams)) {
			return false;
		}
		if (queryParams == null) {
			if (other.queryParams != null) {
				return false;
			}
		} else if (!queryParams.equals(other.queryParams)) {
			return false;
		}
		if (websocket == null) {
			if (other.websocket != null) {
				return false;
			}
		} else if (!websocket.equals(other.websocket)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Request [headers=" + headers + ", queryParams=" + queryParams + ", pathParams=" + pathParams
				+ ", bodyParams=" + bodyParams + ", body=" + Arrays.toString(body) + ", websocket=" + websocket
				+ ", data=" + data + "]";
	}
	
}
