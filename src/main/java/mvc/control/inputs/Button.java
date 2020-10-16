package mvc.control.inputs;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.control.Html;

public class Button implements Jsonable {
		
	private final String url;
	private String confirmation = null;
	private String title = null;
	private boolean ajax = false;
	private String method = "get";
	private Html renderer = null;
	
	public Button(String url) {
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

	public Button setAjax(boolean ajax) {
		this.ajax = ajax;
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
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("url", url);
		json.put("ajax", ajax);
		if (title != null) {
			json.put("title", title);
		}
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		return toJson(json);
	}
	
}
