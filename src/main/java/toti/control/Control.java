package toti.control;

import java.util.Map;

import ji.json.JsonWritter;

public interface Control {

	String getType();
	
	default String toJson(Map<String, Object> json) {
		JsonWritter writer = new JsonWritter();
		return writer.write(json);
	}
	
	static String escapeJs(String text) {
		return text.replaceAll("'", "\'").replaceAll("\\\"", "\\\"");
	}
	
}
