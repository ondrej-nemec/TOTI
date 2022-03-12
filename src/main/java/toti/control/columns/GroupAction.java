package toti.control.columns;

import java.util.HashMap;
import java.util.Map;

import ji.json.Jsonable;
import toti.control.Control;

public class GroupAction implements Jsonable {
	
	private final String title;
	private final String link;
	private boolean async = true;
	private String method = "get";
	private String confirmation;
	private String onFailure;
	private String onSuccess;
	
	public GroupAction(String title, String link) {
		this.title = title;
		this.link = link;
	}

	public GroupAction setConfirmation(String confirm) {
	    this.confirmation = Control.escapeJs(confirm);
		return this;
	}

	public GroupAction setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public GroupAction setMethod(String method) {
		this.method = method;
		return this;
	}

	public GroupAction setOnFailure(String onFailure) {
		this.onFailure = onFailure;
		return this;
	}

	public GroupAction setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
		return this;
	}

	@Override
	public Object toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("async", async);
		json.put("title", title);
		json.put("link", link);
		json.put("method", method);
		if (confirmation != null) {
			json.put("submitConfirmation", confirmation);
		}
		if (onSuccess != null) {
			json.put("onSuccess", onSuccess);
		}
		if (onFailure != null) {
			json.put("onFailure", onFailure);
		}
		return json;
	}

}
