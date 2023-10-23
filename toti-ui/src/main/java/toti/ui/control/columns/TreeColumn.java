package toti.ui.control.columns;

import java.util.HashMap;
import java.util.Map;

public class TreeColumn implements Column {
	
	private String title;
	private int expandLevel = 2;
	private final String rowIdentifier;
	private final String parentIdentifier;
	
	public TreeColumn(String rowIdentifier, String parentIdentifier) {
		this.rowIdentifier = rowIdentifier;
		this.parentIdentifier = parentIdentifier;
	}
	
	public TreeColumn setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public TreeColumn setExpandLevel(int expandLevel) {
		this.expandLevel = expandLevel;
		return this;
	}
	
	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", rowIdentifier + "_" + parentIdentifier);
		json.put("type", "tree");
		json.put("title", title);
		json.put("identifier", rowIdentifier);
		json.put("parent", parentIdentifier);
		json.put("expandLevel", expandLevel);
		json.put("useSorting", false);
		return json;
	}

}
