package toti.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.control.Control;
import toti.url.Link;

public class Button implements Input {
	
	private final String name;
	private final String url;
	private String confirmation = null;
	private String title = null;
	private String tooltip = null;
	private String icon = null;
	private boolean async = false;
	private String method = "get";
	private Map<String, String> requestParams = new HashMap<>();
	private final Map<String, String> params = new HashMap<>();
	private String onFailure;
	private String onSuccess;
	private String condition = null;
	private boolean evaluate = false;
	private List<String> classes = new LinkedList<>();

	public static Button create(String url, String name) {
		return new Button(url, name, false);
	}

	public static Button create(String url, String name, boolean allowOutOfAppLink) {
		return new Button(url, name, allowOutOfAppLink);
	}
	
	private Button(String url, String name, boolean outOfAppLink) {
		if (!outOfAppLink && !Link.isRelative(url)) {
			throw new RuntimeException("Open redirection is not allowed");
		}
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
	
	public Button setConfirmation(String confirm) {
		this.confirmation = Control.escapeJs(confirm);
		return this;
	}

	public Button setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Button setIcon(String icon) {
		this.icon = icon;
		return this;
	}
	
	public Button setTooltip(String tooltip) {
		this.tooltip = tooltip;
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
	
	public Button addClass(String clazz) {
		classes.add(clazz);
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
		json.put("class", classes);
		if (title != null) {
			json.put("value", title);
		}
		if (tooltip != null) {
            json.put("title", tooltip);
        }
        if (icon != null) {
            json.put("icon", icon);
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
		json.putAll(params);
		return json;
	}

}
