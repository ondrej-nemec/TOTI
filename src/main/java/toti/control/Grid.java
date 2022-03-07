package toti.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.json.Jsonable;
import toti.control.columns.Column;
import toti.control.columns.GroupAction;

public class Grid implements Control {

	private final String loadDataUrl;
	private final String loadDataMethod;
	private String uniqueRowIdentifier = "id";
	private boolean useRowSelection = false;
	private String onRowRenderer = null;
	private final List<Jsonable> columns = new LinkedList<>();
	private final List<Jsonable> actions = new LinkedList<>();
	
	// paging
	private List<Integer> pagesSizes = Arrays.asList(10, 20, 50, 100);
	private Integer defaultPageSize = null;
	private Integer pagesButtonCount = 5;
	
	public Grid(String loadDataUrl, String loadDataMethod) {
		this.loadDataMethod = loadDataMethod;
		this.loadDataUrl = loadDataUrl;
	}
	
	public Grid setPagesSizes(List<Integer> pagesSizes) {
		this.pagesSizes = pagesSizes;
		return this;
	}
	
	public Grid setPagesSizes(List<Integer> pagesSizes, Integer defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
		this.pagesSizes = pagesSizes;
		return this;
	}
	
	public Grid setPagesButtonCount(Integer pagesButtonCount) {
		this.pagesButtonCount = pagesButtonCount;
		return this;
	}
	
	public Grid setUniqueRowIdentifier(String uniqueRowIdentifier) {
		this.uniqueRowIdentifier = uniqueRowIdentifier;
		return this;
	}
	
	public Grid useRowSelection(boolean useSelectionRow) {
		this.useRowSelection = useSelectionRow;
		return this;
	}
	
	public Grid setOnRowRenderer(String onRowRenderer) {
		this.onRowRenderer = onRowRenderer;
		return this;
	}
	
	public Grid addColumn(Column column) {
		columns.add(column);
		return this;
	}
	
	public Grid addAction(GroupAction action) {
		actions.add(action);
		return this;
	}

	@Override
	public Map<String, Object> toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("dataLoadUrl", loadDataUrl);
		json.put("dataLoadMethod", loadDataMethod);
		json.put("identifier", uniqueRowIdentifier);
		json.put("useRowSelection", useRowSelection);
		if (onRowRenderer != null) {
			json.put("onRowRenderer", onRowRenderer);
		}
		json.put("columns", columns);
		
		if (pagesSizes != null && !pagesSizes.isEmpty()) {
			Map<String, Object> pages = new HashMap<>();
			json.put("pages", pages);
			pages.put("pagesSizes", pagesSizes);
			pages.put(
				"defaultSize", 
				defaultPageSize == null || !pagesSizes.contains(defaultPageSize) ? pagesSizes.get(0) : defaultPageSize
			);
		}
		if (pagesButtonCount != null) {
			json.put("pagesButtonCount", pagesButtonCount);
		}
		
		json.put("actions", actions);
		return json;
	}
	
	@Override
	public String toString() {
		return toJs();
	}

	@Override
	public String getType() {
		return "Grid";
	}
	
}
