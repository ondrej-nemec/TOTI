package toti.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.exceptions.LogicException;
import ji.json.Jsonable;
import toti.control.columns.Column;
import toti.control.columns.GroupAction;
import toti.control.columns.TreeColumn;

public class Grid implements Control {

	private final String loadDataUrl;
	private final String loadDataMethod;
	private boolean useRowSelection = false;
	private final List<Jsonable> columns = new LinkedList<>();
	private final List<Jsonable> actions = new LinkedList<>();
	
	// paging
	private List<Integer> pagesSizes = Arrays.asList(10, 20, 50, 100); // optional
	private int pageSize = 20; // required, negative means all
	private int pagesButtonCount = 5; // required, 0 means no buttons
	private boolean useLoadButton = false;
	
	private long refreshInterval = 0L; // in ms, 0 means no refresh
	private String rowRenderer = null;
	
	private String beforeRender = null;
	private String afterRender = null;
	private String beforeBind = null;
	private String afterBind = null;
	
	private int treeColumnIndex = -1;
	
	public Grid(String loadDataUrl, String loadDataMethod) {
		this.loadDataMethod = loadDataMethod;
		this.loadDataUrl = loadDataUrl;
	}
	
	public Grid setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	
	public Grid setPagesSizes(List<Integer> pagesSizes) {
		this.pagesSizes = pagesSizes;
		if (pagesSizes != null && !pagesSizes.contains(pageSize)) {
			pageSize = pagesSizes.get(0);
		}
		return this;
	}
	
	public Grid setUseLoadButton(boolean useLoadButton) {
		this.useLoadButton = useLoadButton;
		return this;
	}
	
	public Grid setPagesSizes(List<Integer> pagesSizes, int defaultPageSize) {
		this.pageSize = defaultPageSize;
		this.pagesSizes = pagesSizes;
		return this;
	}
	
	public Grid setPagesButtonCount(int pagesButtonCount) {
		this.pagesButtonCount = pagesButtonCount;
		return this;
	}
	
	public Grid useRowSelection(boolean useSelectionRow) {
		this.useRowSelection = useSelectionRow;
		return this;
	}
	
	public Grid setRowRenderer(String rowRenderer) {
		this.rowRenderer = rowRenderer;
		return this;
	}
	
	public Grid addColumn(Column column) {
		columns.add(column);
		return this;
	}
	
	public Grid addColumn(TreeColumn column) {
		if (treeColumnIndex > 0) {
			throw new LogicException("Grid can contains only one TreeColumn");
		}
		treeColumnIndex = columns.size();
		columns.add(column);
		return this;
	}
	
	public Grid addAction(GroupAction action) {
		actions.add(action);
		return this;
	}
	
	public Grid setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }
	
	public Grid setBeforeRender(String beforeRender) {
		this.beforeRender = beforeRender;
        return this;
	}
	
	public Grid setAfterRender(String afterRender) {
		this.afterRender = afterRender;
        return this;
	}
	
	public Grid setBeforeBind(String beforeBind) {
		this.beforeBind = beforeBind;
        return this;
	}
	
	public Grid setAfterBind(String afterBind) {
		this.afterBind = afterBind;
        return this;
	}

	@Override
	public Map<String, Object> toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("dataLoadUrl", loadDataUrl);
		json.put("dataLoadMethod", loadDataMethod);
		json.put("useRowSelection", useRowSelection);
		json.put("columns", columns);
		json.put("treeColumnIndex", treeColumnIndex);
		
		json.put("pageSize", pageSize);
		
		// defaultSize renamed to pageSize and is required
		// pageSizes is optional
		if (pagesSizes != null && !pagesSizes.isEmpty()) {
			json.put("pagesSizes", pagesSizes);
		}
		if (rowRenderer != null) {
            json.put("rowRenderer", rowRenderer);
        }
		json.put("paggingButtonsCount", pagesButtonCount);
		json.put("useLoadButton", useLoadButton);
		json.put("refresh", refreshInterval);
		
		if (beforeRender != null) {
			json.put("beforeRender", beforeRender);
		}
		if (afterRender != null) {
			json.put("afterRender", afterRender);
		}
		if (beforeBind != null) {
			json.put("beforeBind", beforeBind);
		}
		if (afterBind != null) {
			json.put("afterBind", afterBind);
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
