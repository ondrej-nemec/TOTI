package toti.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import toti.validation.ItemRules;
import toti.validation.Validator;

public class GridOptions implements Entity {
	
	@MapperParameter({@MapperType("pageIndex")})
	private Integer pageIndex;
	@MapperParameter({@MapperType("pageSize")})
	private Integer pageSize;
	@MapperParameter({@MapperType("filters")})
	//private List<Filter> filters = new LinkedList<>();
	private Map<String, Filter> filters = new HashMap<>();
	@MapperParameter({@MapperType("sorting")})
	private Map<String, Sort> sorting = new HashMap<>();
	//private List<Sort> sorting = new LinkedList<>();
	
	public GridOptions() {}

	public GridOptions(Integer pageIndex, Integer pageSize, Map<String, Filter> filters, Map<String, Sort> sorting) {
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.filters = filters;
		this.sorting = sorting;
	}
	
	public Integer getPageIndex() {
		return pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	
	public Map<String, Filter> getFilters() {
		return filters;
	}
	public Map<String, Sort> getSorting() {
		return sorting;
	}
	
	public boolean containsFilter(String name) {
		return filters.containsKey(name);
	}
	public boolean containsSorting(String name) {
		return sorting.containsKey(name);
	}
	
	public void addFilter(String name, FilterMode mode, Object value) {
		filters.put(name, new Filter(name, mode, value));
	}
	
	public void addSorting(String name, boolean isDesc) {
		sorting.put(name, new Sort(name, isDesc));
	}
	
	public static Validator getValidator(List<GridColumn> gridColumns) {
		Validator filters = new Validator(true);
		Validator sorting = new Validator(true);
		gridColumns.forEach((column)->{
			if (column.isUseInFilter()) {
				filters.addRule(
					ItemRules.forName(column.getName(), false).setType(column.getType())
					.setChangeValue((v)->{
						if (v == null) {
							return null;
						}
						return new Filter(column.getName(), column.getFilterMode(), v);
					})
				);
			}
			if (column.isUseInSorting()) {
				sorting.addRule(
					ItemRules.forName(column.getName(), false).setAllowedValues(Arrays.asList("DESC", "ASC"))
					.setChangeValue((v)->{
						if (v == null) {
							return null;
						}
						return new Sort(column.getSortingName(), v.equals("DESC"));
					})
				);
			}
		});
		return new Validator(true)
			.addRule(ItemRules.forName("pageIndex", true))
			.addRule(ItemRules.forName("pageSize", true))
			.addRule(ItemRules.forName("filters", true).setMapSpecification(filters))
			.addRule(ItemRules.forName("sorting", true).setMapSpecification(sorting));
	}

}
