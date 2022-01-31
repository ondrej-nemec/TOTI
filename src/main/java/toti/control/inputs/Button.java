package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

import toti.control.Control;

public class Button implements Input {
	
	private final String name;
	private final String url;
	private String confirmation = null;
	private String title = null;
	private boolean async = false;
	private String method = "get";
	private Map<String, String> requestParams = new HashMap<>();
	private final Map<String, String> params = new HashMap<>();
	private String onFailure;
	private String onSuccess;
	private ButtonType type = ButtonType.BASIC;
	private String condition = null;
	private boolean evaluate = false;

	public static Button create(String url, String name) {
		return new Button(url, name);
	}
	
	private Button(String url, String name) {
		this.url = url;
		this.name = name;
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
		this.confirmation = Control.escapeJs(confirm);
		return this;
	}

	public Button setTitle(String title) {
		this.title = title;
		return this;
	}

	public Button setAsync(boolean async) {
		this.async = async;
		return this;
	}
	
	public Button addRequestParam(String name, String value) {
		this.requestParams.put(name, value);
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
	
	public Button setCondition(String condition) {
		return setCondition(condition, false);
	}
	
	public Button setCondition(String condition, boolean evaluate) {
		this.condition = condition;
		this.evaluate = evaluate;
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("href", url);
		json.put("async", async);
		json.put("type", "button");
		json.put("name", name);
		json.put("id", name);
		json.put("method", method);
		json.put("requestParams", requestParams);
		if (title != null) {
			json.put("value", title);
		}
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		if (onFailure != null) {
			json.put("onError", onFailure);
		}
		if (onSuccess != null) {
			json.put("onSuccess", onSuccess);
		}
		if (condition != null) {
			json.put("condition", condition);
			json.put("evaluate", evaluate);
		}
		json.put("style", type.toString().toLowerCase());
		json.putAll(params);
		return json;
	}

}
