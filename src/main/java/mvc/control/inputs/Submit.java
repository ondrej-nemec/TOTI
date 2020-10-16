package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;

public class Submit implements Jsonable, Input {
	
	private final String type;
	private final String title;
	private String redirect;
	private String confirmation;
	
	public Submit(String title) {
		this.type = "submit";
		this.title = title;
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

	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("type", type);
		json.put("value", title);
		if (redirect != null) {
			json.put("redirect", redirect);
		}
		if (confirmation != null) {
			json.put("value", confirmation);
		}
		return toJson(json);
	}
	
}
