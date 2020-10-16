package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;

public class Hidden implements Jsonable, Input {
	
	private final String name;
	private final String id;
	private final String type;
	private String value = null;
	
	public Hidden(String name, String id) {
		this.name = name;
		this.id = id;
		this.type = "hidden";
	}
	
	public Hidden setDefaultValue(String value) {
		this.value = value;
		return this;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		if (value != null) {
			json.put("value", value);
		}
		return toJson(json);
	}

}
