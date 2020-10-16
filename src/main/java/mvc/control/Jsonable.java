package mvc.control;

import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonStream;
import json.OutputJsonWritter;
import json.providers.OutputStringProvider;

public interface Jsonable {

	// TODO to ji???
	default String toJson(Map<String, Object> json) {
		OutputJsonWritter writer = new OutputJsonWritter();
		OutputStringProvider provider = new OutputStringProvider();
		try {
			writer.write(new OutputJsonStream(provider), json);
		} catch (JsonStreamException e) {/*ignored, no reason for occuring*/}
		return provider.getJson();
	}
	
	default String escapeJs(String text) {
		return text.replaceAll("'", "\'").replaceAll("\\\"", "\\\"");
	}
}
