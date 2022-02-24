package toti.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.json.Jsonable;

public class GridDataSet<T extends Entity> implements Jsonable {
	
	private final List<T> items;
	private final int totalCount;
	private final int pageIndex;
	
	public static <T extends Entity> GridDataSet<T> create(List<T> items, int pageIndex, int pageSize) {
		GridRange range = GridRange.create(items.size(), pageIndex, pageSize);
		List<T> dataset = items.subList(range.getOffset(), range.getLimit() + range.getLimit() + 1);
		return new GridDataSet<>(
			dataset, 
			items.size(),
			range.getPageIndex()
		);
	}

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

	@Override
	public String toString() {
		return toJson().toString();
	}
	
}
