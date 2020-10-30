package mvc.control.columns;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Jsonable;
import mvc.templating.Template;

public class GroupAction implements Jsonable {
	
	private final String title;
	private final String link;
	private boolean ajax = true;
	private String method = "get";
	private String confirmation;
	
	public GroupAction(String title, String link) {
		this.title = title;
		this.link = link;
	}

	public GroupAction setConfirmation(String confirm) {
		this.confirmation = escapeJs(confirm);
		return this;
	}

	public GroupAction setAjax(boolean ajax) {
		this.ajax = ajax;
		return this;
	}

	public GroupAction setMethod(String method) {
		this.method = method;
		return this;
	}

	// @Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("ajax", ajax);
		json.put("title", Template.escapeVariable(title));
		json.put("link", link);
		json.put("method", method);
		if (confirmation != null) {
			json.put("confirmation", confirmation);
		}
		return json;
	}

}
