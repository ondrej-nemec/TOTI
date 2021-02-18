package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Reset implements Input {

	private String title = null;
	private final Map<String, String> params = new HashMap<>();

	public static Reset create() {
		return new Reset();
	}
	
	private Reset() {}
	
	public Reset setTitle(String title) {
		this.title = title;
		return this;
	}

	public Reset addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("type", "reset");
		json.put("name", "");
		if (title != null) {
			json.put("value", title);
		}
		json.putAll(params);
		return json;
	}
}
