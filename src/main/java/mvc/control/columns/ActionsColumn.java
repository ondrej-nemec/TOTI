package mvc.control.columns;

import java.util.HashMap;
import java.util.Map;

import json.JsonStreamException;
import mvc.control.Jsonable;
import mvc.templating.Template;

public class ActionsColumn implements Jsonable, Column {

	private final String name;
	private final String type;
	private final boolean useSorting = false;
	private String title;
		
	public ActionsColumn(String name) {
		this.name = name;
		this.type = "actions";
		this.title = name;
	}
	
	public ActionsColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	public String toJsonString() throws JsonStreamException {
		Map<String, Object> json = new HashMap<>();
		json.put("name", Template.escapeVariable(name));
		json.put("type", type);
		json.put("title", Template.escapeVariable(title));
		json.put("sorting", useSorting);
		return toJson(json);
	}
	
}
