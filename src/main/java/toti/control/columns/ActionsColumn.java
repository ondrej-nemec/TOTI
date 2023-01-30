package toti.control.columns;

import java.util.HashMap;
import java.util.Map;

public class ActionsColumn implements Column {

	private final String name;
	private final String type;
	private String title;
	private String identifier = "id";
		
	public ActionsColumn(String name) {
		this.name = name;
		this.type = "actions";
		this.title = name;
	}
	
	public ActionsColumn setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ActionsColumn setUniqueRowIdentifier(String uniqueRowIdentifier) {
		this.identifier = uniqueRowIdentifier;
		return this;
	}
	
	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("identifier", identifier);
		json.put("type", type);
		json.put("title", title);
		json.put("useSorting", false);
		return json;
	}
	
}
