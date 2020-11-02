package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.control.Html;

public class Button implements Input, Jsonable {
		
	private final String url;
	private String confirmation = null;
	private String title = null;
	private boolean ajax = false;
	private String method = "get";
	private Html renderer = null;
	private final Map<String, String> params = new HashMap<>();
	private boolean preventDefault = false;
	
	public static Button create(String url) {
		return new Button(url);
	}
	
	private Button(String url) {
		this.url = url;
	}

	public Button setConfirmation(String confirm) {
		this.confirmation = escapeJs(confirm);
		return this;
	}

	public Button setTitle(String title) {
		this.title = title;
		return this;
	}

	public Button setPreventDefault(boolean preventDefault) {
		this.preventDefault = preventDefault;
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

	public Button setRenderer(Html renderer) {
		this.renderer = renderer;
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("href", url);
		json.put("ajax", ajax);
		json.put("type", "button");
		json.put("method", method);
		if (title != null) {
			json.put("title", title);
		}
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		json.put("preventDefault", preventDefault);
		json.put("params", params);
		return json;
	}

}
