package toti.control.columns;

import java.util.HashMap;
import java.util.Map;

public class ValueColumn implements Column {
	
	private final String name;
	private String title;
	
	private boolean useSorting = false;
	private Boolean defaultSort = null;
	private Filter filter = null;
	private String renderer = null;
	
	public ValueColumn(String name) {
		this.name = name;
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
	
	public ValueColumn setDefaultSort(Boolean defaultSort) {
		this.defaultSort = defaultSort;
		return this;
	}

	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", "value");
		json.put("title", title);
		json.put("useSorting", useSorting);
		json.put("defSort", defaultSort);
		if (filter != null) {
			/*Map<String, Object> filterConf = new HashMap<>(filter.getFilterSettings());
			json.put("filter", filterConf);
			filterConf.put("type", filter.getType());*/
			json.put("filter", filter.getFilterSettings());
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		return json;
	}
	
}
