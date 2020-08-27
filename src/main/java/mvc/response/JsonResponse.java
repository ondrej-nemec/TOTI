package mvc.response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonStream;
import json.providers.OutputReaderProvider;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class JsonResponse implements Response {
	
	private final Map<String, Object> json;
	private final StatusCode code;
	private final String charset;

	public JsonResponse(StatusCode code, Map<String, Object> json, String charset) {
		this.json = json;
		this.code = code;
		this.charset = charset;
	}

	@Override
	public RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator) {
		List<String> h = new LinkedList<>(header);
		h.add("Content-Type: application/json; charset=" + charset);
		return RestApiResponse.textResponse(code, h, (bw)->{
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
