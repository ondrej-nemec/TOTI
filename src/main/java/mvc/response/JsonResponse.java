package mvc.response;

import java.util.List;
import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonStream;
import json.providers.OutputReaderProvider;
import mvc.ResponseHeaders;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class JsonResponse implements Response {
	
	private final Map<String, Object> json;
	private final StatusCode code;

	public JsonResponse(StatusCode code, Map<String, Object> json) {
		this.json = json;
		this.code = code;
	}

	@Override
	public void addParam(String name, Object value) {}

	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String templateDir, String charset) {
		return RestApiResponse.textResponse(
			code,
			header.getHeaders("Content-Type: application/json; charset=" + charset),
			(bw)->{
				try {
					OutputJsonStream stream = new OutputJsonStream(new OutputReaderProvider(bw));
					stream.startDocument();
					for (String key : json.keySet()) {
						writeObject(stream, json.get(key), key);
					}
					stream.endDocument();
				} catch (JsonStreamException e) {
					throw new RuntimeException(e);
				}
		});
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
