package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.Jsonable;

public class Button implements Input, Jsonable {
	
	private final String url;
	private String confirmation = null;
	private String title = null;
	private boolean ajax = false;
	private String method = "get";
	private final Map<String, String> params = new HashMap<>();
	private String onFailure;
	private String onSuccess;
	private ButtonType type = ButtonType.BASIC;
	
	public static Button create(String url) {
		return new Button(url);
	}
	
	private Button(String url) {
		this.url = url;
	}

	public Button setOnFailure(String onFailure) {
		this.onFailure = onFailure;
		return this;
	}

	public Button setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
		return this;
	}
	
	public Button setType(ButtonType type) {
		this.type = type;
		return this;
	}
	
	public Button setConfirmation(String confirm) {
		this.confirmation = escapeJs(confirm);
		return this;
	}

	public Button setTitle(String title) {
		this.title = title;
		return this;
	}

	public Button setAjax(boolean ajax) {
		this.ajax = ajax;
		return this;
	}

	public Button addParam(String name, String value) {
		params.put(name, value);
		return this;
	}

	public Button setMethod(String method) {
		this.method = method;
		return this;
	}

/*	public Button setRenderer(Html renderer) {
		this.renderer = renderer;
		return this;
	}*/
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("href", url);
		json.put("ajax", ajax);
		json.put("type", "button");
		json.put("name", "");
		json.put("method", method);
		if (title != null) {
			json.put("value", title);
		}
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		/*if (renderer != null) {
			json.put("renderer", renderer);
		}*/
		if (onFailure != null) {
			json.put("onError", onFailure);
		}
		if (onSuccess != null) {
			json.put("onSuccess", onSuccess);
		}
		json.put("style", type.toString().toLowerCase());
		json.putAll(params);
		return json;
	}

}
