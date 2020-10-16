package mvc.control.columns;

import java.util.HashMap;
import java.util.Map;

import json.JsonStreamException;
import mvc.control.Jsonable;
import mvc.templating.Template;

public class Action implements Jsonable {
	
	private final String title;
	private final String link;
	private boolean ajax = true;
	private String method = "get";
	private String confirmation;
	
	public Action(String title, String link) {
		this.title = title;
		this.link = link;
	}

	public Action setConfirmation(String confirm) {
		this.confirmation = escapeJs(confirm);
		return this;
	}

	public Action setAjax(boolean ajax) {
		this.ajax = ajax;
		return this;
	}

	public Action setMethod(String method) {
		this.method = method;
		return this;
	}

	public String toJsonString() throws JsonStreamException {
		Map<String, Object> json = new HashMap<>();
		json.put("ajax", ajax);
		json.put("title", Template.escapeVariable(title));
		json.put("link", link);
		json.put("method", method);
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		return toJson(json);
	}

}
