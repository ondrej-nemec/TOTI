package toti.answers.response;

import java.util.List;
import java.util.Map;

import ji.json.JsonStreamException;
import ji.json.OutputJsonStream;
import ji.json.JsonWritter;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.answers.Headers;
import toti.answers.request.Identity;

public class JsonResponse implements Response {
	
	private final Object json;
	private final StatusCode code;
	private final Headers headers;

	public JsonResponse(StatusCode code, Headers headers, Object json) {
		this.json = json;
		this.code = code;
		this.headers = headers;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers header,
			Identity identity,
			ResponseContainer container,
			String charset) {
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		response.setHeaders(header.getHeaders());
		response.addHeader("Content-Type", "application/json; charset=" + charset);
		response.setBody(createResponse().getBytes());
		response.setHeaders(this.headers.getHeaders());
		return response;
	}
	
	private String createResponse() {
		try {
			JsonWritter writter = new JsonWritter();
			return writter.write(json);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}
	
	private void write(OutputJsonStream stream, Map<String, Object> objects, String name) throws JsonStreamException {
		if (name == null) {
			stream.writeObjectStart();
		} else {
			stream.writeObjectStart(name);
		}
		for (String key : objects.keySet()) {
			writeObject(stream, objects.get(key), key);
		}
		stream.writeObjectEnd();
	}
	
	private void write(OutputJsonStream stream, List<Object> list, String name) throws JsonStreamException {
		if (name == null) {
			stream.writeListStart();
		} else {
			stream.writeListStart(name);
		}
		for (Object o : list) {
			writeObject(stream, o, null);
		}
		stream.writeListEnd();
	}
	
	
	@SuppressWarnings("unchecked")
	private void writeObject(OutputJsonStream stream, Object value, String name) throws JsonStreamException {
		if (value == null) {
			if (name == null) {
				stream.writeListValue(value);
			} else {
				stream.writeObjectValue(name, value);
			}
		} else if (value instanceof List) {
			write(stream, (List<Object>)value, name);
		} else if (value instanceof Map) {
			write(stream, (Map<String, Object>)value, name);
		} else {
			if (name == null) {
				stream.writeListValue(value);
			} else {
				stream.writeObjectValue(name, value);
			}
		}	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((json == null) ? 0 : json.hashCode());
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
		JsonResponse other = (JsonResponse) obj;
		if (code != other.code) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (json == null) {
			if (other.json != null) {
				return false;
			}
		} else if (!json.equals(other.json)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "JsonResponse [json=" + json + ", code=" + code + ", headers=" + headers + "]";
	}
	
}
