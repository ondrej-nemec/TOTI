package toti.tcpip.client;

import toti.lib.common.structures.MapDictionary;
import toti.tcpip.enums.HttpMethod;

public class ExchangeRequest extends Exchange {
	
	private final HttpMethod method;
	private final Protocol protocol;
	private final String uri; // full uri with parameter
	
	private String plainUri;
	private MapDictionary<String> queryParams;
	
	public ExchangeRequest(HttpMethod method, String uri, Protocol protocol) {
		super();
		this.method = method;
		this.protocol = protocol;
		this.uri = uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public String getUri() {
		return uri;
	}
	
	public String getPlainUri() {
		return plainUri;
	}
	
	public MapDictionary<String> getQueryParameters() {
		return queryParams;
	}

	public void setUriParams(String plainUri, MapDictionary<String> queryParams) {
		this.plainUri = plainUri;
		this.queryParams = queryParams;
	}
	
	@Override
	public String getFirstLine() {
		return String.format("%s %s %s", method, uri, protocol);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((plainUri == null) ? 0 : plainUri.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((queryParams == null) ? 0 : queryParams.hashCode());
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
		ExchangeRequest other = (ExchangeRequest) obj;
		if (method != other.method) {
			return false;
		}
		if (plainUri == null) {
			if (other.plainUri != null) {
				return false;
			}
		} else if (!plainUri.equals(other.plainUri)) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				System.err.println("protocol 1");
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		if (queryParams == null) {
			if (other.queryParams != null) {
				return false;
			}
		} else if (!queryParams.equals(other.queryParams)) {
			return false;
		}
		return true;
	}
	
}
