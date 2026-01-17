package toti.ui.control;

import java.util.Map;

import toti.lib.files.json.JsonWritter;
import toti.lib.files.json.Jsonable;

public interface Control extends Jsonable {

	String getType();
	
	Map<String, Object> toJson();
	
	default String toJs() {
		JsonWritter writer = new JsonWritter();
		return String.format(
			"new Toti%s(%s)",
			getType(),
			writer.write(toJson())
		);
	}
	
	static String escapeJs(String text) {
		return text.replaceAll("'", "\'").replaceAll("\\\"", "\\\"");
	}
	
}
