package mvc.control;

import java.util.HashMap;
import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonStream;
import json.OutputJsonWritter;
import json.providers.OutputStringProvider;
import mvc.templating.Template;

public abstract class Column {
	
	private final String name;
	private final String type; // value, buttons, actions
	private String title;
	private boolean useSorting = false;
	
	private final String filterType;
	private boolean useFilter = false;
	
	private Html renderer = null;
	
	public Column(String name, String type, String filterType) {
		this.name = name;
		this.type = type;
		this.title = name;
		this.filterType = filterType;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setUseSorting(boolean useSorting) {
		this.useSorting = useSorting;
	}

	public void setUseFilter(boolean useFilter) {
		this.useFilter = useFilter;
	}

	public void setRenderer(Html renderer) {
		this.renderer = renderer;
	}

	public String toJsonString() throws JsonStreamException {
		Map<String, Object> json = new HashMap<>();
		json.put("name", Template.escapeVariable(name));
		json.put("type", type);
		json.put("title", Template.escapeVariable(title));
		json.put("sorting", useSorting);
		if (useFilter) {
			json.put("filter", filterType);
		}
		if (renderer != null) {
			json.put("renderer", renderer);
		}
		OutputJsonWritter writer = new OutputJsonWritter();
		OutputStringProvider provider = new OutputStringProvider();
		OutputJsonStream stream = new OutputJsonStream(provider);
		writer.write(stream, json);
		return provider.getJson();
	}
	
	public abstract Map<String, Object> getParams();
	
}
