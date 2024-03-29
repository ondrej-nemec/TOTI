package toti.response;

import java.util.List;
import java.util.Map;

import ji.json.JsonStreamException;
import ji.json.OutputJsonStream;
import ji.json.JsonWritter;
import ji.socketCommunication.http.StatusCode;
import toti.Headers;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import ji.translator.Translator;

public class JsonResponse implements Response {
	
	private final Object json;
	private final StatusCode code;

	public JsonResponse(StatusCode code, Object json) {
		this.json = json;
		this.code = code;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			String protocol,
			Headers header,
			TemplateFactory templateFactory, 
			Translator translator, 
			Authorizator authorizator,
			Identity identity, MappedUrl current,
			String charset) {
		header.addHeader("Content-Type", "application/json; charset=" + charset);
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		response.setHeaders(header.getHeaders());
		response.setBody(createResponse().getBytes());
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
	
}
