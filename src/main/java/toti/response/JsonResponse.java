package toti.response;

import java.util.List;
import java.util.Map;

import ji.json.JsonStreamException;
import ji.json.OutputJsonStream;
import ji.json.providers.OutputWriterProvider;
import ji.json.JsonWritter;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
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
	public void addParam(String name, Object value) {}

	@Override
	public RestApiResponse getResponse(
			ResponseHeaders header,
			TemplateFactory templateFactory, 
			Translator translator, 
			Authorizator authorizator,
			Identity identity, MappedUrl current,
			String charset) {
		header.addHeader("Content-Type: application/json; charset=" + charset);
		return RestApiResponse.textResponse(
			code,
			header.getHeaders(),
			(bw)->{
				try {
					OutputJsonStream stream = new OutputJsonStream(new OutputWriterProvider(bw));
					JsonWritter writter = new JsonWritter();
					writter.write(stream, json);
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
