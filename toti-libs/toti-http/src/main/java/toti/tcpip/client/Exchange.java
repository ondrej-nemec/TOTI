package toti.tcpip.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.common.structures.DictionaryValue;
import toti.tcpip.structures.RequestParameters;

public abstract class Exchange {
	
	private final Map<String, List<Object>> headers = new HashMap<>();
	
	private BodyType type = BodyType.BASIC;
	private RequestParameters parameters = new RequestParameters();
	private byte[] body;
	private WebSocket websocket;

	public abstract String getFirstLine();
	
	/**** headers section *****/
	
	public void addHeader(String name, Object value) {
		String headerName = name.toLowerCase();
		if (!headers.containsKey(headerName)) {
			headers.put(headerName, new LinkedList<>());
		}
		headers.get(headerName).add(value);
	}
	
	public Map<String, List<Object>> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, List<Object>> headers) {
		this.headers.putAll(headers);
	}
	
	public List<Object> getHeaders(String name) {
		return headers.get(name.toLowerCase());
	}
	
	public Object getHeader(String name)  {
		String headerName = name.toLowerCase();
		if (headers.containsKey(headerName) && headers.get(headerName).size() > 0) {
			return headers.get(headerName).get(0);
		}
		return null;
	}
	
	public boolean containsHeader(String name) {
		String headerName = name.toLowerCase();
		return headers.containsKey(headerName) && headers.get(headerName).size() > 0;
	}
	
	public <T> T getHeader(String name, Class<T> clazz) {
		return new DictionaryValue(getHeader(name)).getValue(clazz);
	}
	
	/**** body section ***/
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public RequestParameters getBodyInParameters() {
		return parameters;
	}
	
	public WebSocket getBodyWebsocket() {
		return websocket;
	}
	
	public void setBodyFormData(RequestParameters body) {
		this.parameters = body;
		this.type = BodyType.FORM_DATA;
	}
	
	public void setBodyUrlencoded(RequestParameters body) {
		this.parameters = body;
		this.type = BodyType.URLENCODED_DATA;
	}
	
	public void setBodyWebsocket(WebSocket webSocket) {
		this.websocket = webSocket;
		this.type = BodyType.WEBSOCKET;
	}
	
	public BodyType getType() {
		return type;
	}

	/************/
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getFirstLine());
		b.append("\n");
		headers.forEach((name, list)->{
			list.forEach((value)->{
				b.append(String.format("%s: %s\n", name, value));
			});
		});
		b.append("\n");
		
		if (body == null) {
			b.append(parameters);
		} else {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				os.write(body);
			} catch (IOException e) {}
			b.append(new String(os.toByteArray()));
		}
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(body);
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Exchange other = (Exchange) obj;
		if (!Arrays.equals(body, other.body)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
	
}
