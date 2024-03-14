package toti.ui.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Reset implements Input {

	private final String name;
	private final Map<String, String> params = new HashMap<>();

	public static Reset create(String name) {
		return new Reset(name);
	}
	
	private Reset(String name) {
		this.name = name;
	}

	public Reset addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("type", "reset");
		json.put("name", name);
		json.put("id", name);
		json.putAll(params);
		return json;
	}
}
