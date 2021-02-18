package toti.control;

import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonWritter;

public interface Control {

	String getType();
	
	default String toJson(Map<String, Object> json) {
		OutputJsonWritter writer = new OutputJsonWritter();
		try {
			return writer.write(json);
		} catch (JsonStreamException e) {/*ignored, no reason for occuring*/}
		return "";
	}
	
	static String escapeJs(String text) {
		return text.replaceAll("'", "\'").replaceAll("\\\"", "\\\"");
	}
	
}
