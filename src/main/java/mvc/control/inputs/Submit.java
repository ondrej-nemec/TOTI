package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;

public class Submit implements Jsonable, Input {
	
	private final String type;
	private final String name;
	private final String title;
	private String redirect;
	private String confirmation;
	private boolean ajax = true;
	private final Map<String, String> params = new HashMap<>();
	
	public static Submit create(String title, String name) {
		return new Submit(title, name);
	}
	
	private Submit(String title, String name) {
		this.type = "submit";
		this.title = title;
		this.name = name;
	}

	// after success ajax request, redirect
	public Submit setRedirect(String redirect) {
		this.redirect = redirect;
		return this;
	}

	public Submit setConfirmation(String confirmation) {
		this.confirmation = escapeJs(confirmation);
		return this;
	}

	public Submit addParam(String name, String value) {
		params.put(name, value);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("type", type);
		json.put("value", title);
		json.put("ajax", ajax);
		json.put("id", name);
		params.forEach((key, param)->{
			json.put(key, param);
		});
		if (redirect != null) {
			json.put("redirect", redirect);
		}
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		return json;
	}
	
}
