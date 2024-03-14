package toti.ui.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.answers.router.Link;
import toti.ui.control.Control;

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
	private final Map<String, Object> params = new HashMap<>();
	private String onFailure;
	private String onSuccess;
	private String condition = null;
	private boolean evaluate = false;
	private List<String> classes = new LinkedList<>();
	// TODO vyzkouset value
	
	// grid only
	private boolean isReset = false;
	private boolean isRefresh = false;
	private boolean addFilters = false;

	public static Button create(String url, String name) {
		return new Button(url, name, false);
	}

	public static Button reset(String name) {
		return new Button(null, name, false);
	}

	public static Button create(String url, String name, boolean allowOutOfAppLink) {
		return new Button(url, name, allowOutOfAppLink);
	}
	
	private Button(String url, String name, boolean outOfAppLink) {
		if (url != null && !outOfAppLink && !Link.isRelative(url)) {
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

	public Button addParam(String name, Object value) {
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
	
	public Button setAddFilters(boolean addFilters) {
		this.addFilters = addFilters;
		return this;
	}
	

	public Map<String, Object> getGlobalSettings() {
		Map<String, Object> settings = getInputSettings();
		settings.put("is_reset", isReset);
		settings.put("is_refresh", isRefresh);
		settings.put("addFilters", addFilters);
		return settings;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		// TODO action as string - JS func onClick
		Map<String, Object> action = new HashMap<>();
		action.put("href", url);
		action.put("async", async);
		action.put("method", method);
		action.put("params", requestParams);
		if (confirmation != null) {
			action.put("submitConfirmation", confirmation);
		}
		if (onFailure != null) {
			action.put("onFailure", onFailure);
		}
		if (onSuccess != null) {
			action.put("onSuccess", onSuccess);
		}
		
		Map<String, Object> json = new HashMap<>();
		json.put("type", "button");
		json.put("name", name);
		json.put("action", action);
		json.put("class", classes);
		if (title != null) {
			json.put("value", title);
		}
		if (tooltip != null) {
            json.put("tooltip", tooltip);
        }
        if (icon != null) {
            json.put("icon", icon);
        }
		if (condition != null) {
			json.put("condition", condition);
			json.put("evaluate", evaluate);
		}
		json.putAll(params);
		return json;
	}

}
