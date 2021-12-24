package toti.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.json.Jsonable;

public class GridDataSet<T extends Entity> implements Jsonable {
	
	private final List<T> items;
	private final int totalCount;
	private final int pageIndex;

	public GridDataSet(List<T> items, int totalCount, int pageIndex) {
		this.items = items;
		this.totalCount = totalCount;
		this.pageIndex = pageIndex;
	}

	@Override
	public Object toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("data", items);
		json.put("itemsCount", totalCount);
		json.put("pageIndex", pageIndex);
		return json;
	}

}
