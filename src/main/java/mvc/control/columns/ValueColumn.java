package mvc.control.columns;

import java.util.HashMap;
import java.util.Map;

import json.JsonStreamException;
import mvc.control.Jsonable;
import mvc.control.Html;
import mvc.templating.Template;

public class ValueColumn implements Jsonable, Column {
	
	private final String name;
	private final String type; // value, buttons, actions
	private String title;
	
	private boolean useSorting = false;
	private Filter filter = null;
	private Html renderer = null;
	
	public ValueColumn(String name) {
		this.name = name;
		this.type = "value";
		this.title = name;
	}
	
	public ValueColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	public ValueColumn setUseSorting(boolean useSorting) {
		this.useSorting = useSorting;
		return this;
	}

	public ValueColumn setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	public ValueColumn setRenderer(Html renderer) {
		this.renderer = renderer;
		return this;
	}

	public String toJsonString() throws JsonStreamException {
		Map<String, Object> json = new HashMap<>();
		json.put("name", Template.escapeVariable(name));
		json.put("type", type);
		json.put("title", Template.escapeVariable(title));
		json.put("sorting", useSorting);
		if (filter != null) {
			Map<String, Object> filterConf = new HashMap<>(filter.getFilterSettings());
			json.put("filter", filterConf);
			filterConf.put("type", filter.getType());
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		return toJson(json);
	}
	
}
