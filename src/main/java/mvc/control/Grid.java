package mvc.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mvc.control.columns.GroupAction;
import mvc.control.columns.Column;

public class Grid implements Jsonable, Control {

	private final String loadDataUrl;
	private final String loadDataMethod;
	private String uniqueRowIdentifier = "id";
	//private final List<Column> columns = new LinkedList<>();
	//private final List<Action> actions = new LinkedList<>();
	private final List<Map<String, Object>> columns = new LinkedList<>();
	private final List<Map<String, Object>> actions = new LinkedList<>();
	
	// paging
	private List<Integer> pagesSizes = Arrays.asList(5, 10, 20, 50, 100);
	private Integer defaultPageSize = null;
	private int pagesButtonCount = 5;
	
	public Grid(String loadDataUrl, String loadDataMethod) {
		this.loadDataMethod = loadDataMethod;
		this.loadDataUrl = loadDataUrl;
	}
	
	public Grid addColumn(Column column) {
		columns.add(column.getGridSettings());
		return this;
	}
	
	public Grid addAction(GroupAction action) {
		actions.add(action.getGridSettings());
		return this;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new HashMap<>();
		json.put("dataLoadUrl", loadDataUrl);
		json.put("dataLoadMethod", loadDataMethod);
		json.put("identifier", uniqueRowIdentifier);
		json.put("columns", columns);
		
		Map<String, Object> pages = new HashMap<>();
		json.put("pages", pages);
		pages.put("pagesSizes", pagesSizes);
		if (defaultPageSize == null && !pagesSizes.isEmpty()) {
			defaultPageSize = pagesSizes.get(0);
		}
		pages.put("defaultSize", defaultPageSize);
		pages.put("pagesButtonCount", pagesButtonCount);
		
		Map<String, Object> groupActions = new HashMap<>();
		json.put("actions", groupActions);
		groupActions.put("actionsList", actions);
		groupActions.put("onSuccess", null); // TODO
		groupActions.put("onError", null); // TODO
		return toJson(json);
	}

	@Override
	public String getType() {
		return "Grid";
	}
	
}
