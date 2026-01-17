package toti.extension.ui.backend.grid;

import java.time.LocalDateTime;
import java.util.List;

import toti.extension.ui.backend.Entity;
import toti.extension.validation.ItemRules;
import toti.extension.validation.Validator;
import toti.lib.common.annotations.MapperParameter;
import toti.lib.common.annotations.MapperType;
import toti.lib.common.structures.SortedMap;

public class GridOptions implements Entity {
	
	@MapperParameter({@MapperType("pageIndex")})
	private Integer pageIndex;
	@MapperParameter({@MapperType("pageSize")})
	private Integer pageSize;
	@MapperParameter({@MapperType("filters")})
	private SortedMap<String, Filter> filters = new SortedMap<>();
//	private Map<String, Filter> filters = new HashMap<>();
	@MapperParameter({@MapperType("sorting")})
	private SortedMap<String, Sort> sorting = new SortedMap<>();
//	private Map<String, Sort> sorting = new HashMap<>();
	
	public GridOptions() {}

	public GridOptions(Integer pageIndex, Integer pageSize, SortedMap<String, Filter> filters, SortedMap<String, Sort> sorting) {
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
	
	public SortedMap<String, Filter> getFilters() {
		return filters;
	}
	public SortedMap<String, Sort> getSorting() {
		return sorting;
	}
	
	public boolean containsFilter(String name) {
		return filters.containsKey(name);
	}
	public boolean containsSorting(String name) {
		return sorting.containsKey(name);
	}
	
	public Filter getFilter(String name) {
		return filters.getValue(name);
	}
	
	public Sort getSorting(String name) {
		return sorting.getValue(name);
	}
	
	public void addFilter(String name, FilterMode mode, Object value, boolean isCI, boolean isIgnoreDiactritics) {
		filters.put(name, new Filter(name, mode, value, isCI, isIgnoreDiactritics));
	}
	
	public void addSorting(String name, boolean isDesc) {
		sorting.put(name, new Sort(name, isDesc));
	}
	
	public static Validator getValidator(List<GridColumn> gridColumns) {
		Validator filters = new Validator(true);
		Validator sorting = new Validator(true);
		gridColumns.forEach((column)->{
			if (column.isUseInFilter()) {
				Class<?> type = column.getType();
				if (type.equals(LocalDateTime.class)) {
					 type = String.class;
				}
				filters.addRule(
					 ItemRules.objectRules(column.getName(), false).setType(type)
					 .changeValue((v)->{
						 if (v == null) {
							  return null;
						 }
						 if (column.getType().equals(LocalDateTime.class)) {
							  v = v.toString().replace("T", " ");
						 }
						 return new Filter(
							  column.getName(), column.getFilterMode(), v,
							  column.getType().equals(String.class) ? column.isCI() : false,
							  column.getType().equals(String.class) ? column.isIgnoreDiacritics() : false
						 );
					 })
				);
			}
			if (column.isUseInSorting()) {
				sorting.addRule(
					ItemRules.booleanRules(column.getName(), false)
					//setAllowedValues(Arrays.asList("DESC", "ASC"))
					.changeValue((v)->{
						if (v == null) {
							return null;
						}
						return new Sort(column.getSortingName(), (boolean)v);
					})
				);
			}
		});
		return new Validator(true)
			.addRule(ItemRules.numberRules("pageIndex", true, Integer.class))
			.addRule(ItemRules.numberRules("pageSize", true, Integer.class))
			.addRule(ItemRules.sortedMapRules("filters", true, filters))
			.addRule(ItemRules.sortedMapRules("sorting", true, sorting));
	}

}
