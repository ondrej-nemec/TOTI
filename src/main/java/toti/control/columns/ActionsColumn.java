package toti.control.columns;

import java.util.HashMap;
import java.util.Map;

import toti.templating.Template;

public class ActionsColumn implements Column {

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
	
	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", Template.escapeVariable(name));
		json.put("type", type);
		json.put("title", Template.escapeVariable(title));
		json.put("sorting", useSorting);
		return json;
	}
	
}
