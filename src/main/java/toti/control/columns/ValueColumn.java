package toti.control.columns;

import java.util.HashMap;
import java.util.Map;

public class ValueColumn implements Column {
	
	private final String name;
	private final String type; // value, buttons, actions
	private String title;
	
	private boolean useSorting = false;
	private Filter filter = null;
	private String renderer = null;
	
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

	public ValueColumn setRenderer(String renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", type);
		json.put("title", title);
		json.put("useSorting", useSorting);
		if (filter != null) {
			Map<String, Object> filterConf = new HashMap<>(filter.getFilterSettings());
			json.put("filter", filterConf);
			filterConf.put("type", filter.getType());
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		return json;
	}
	
}
