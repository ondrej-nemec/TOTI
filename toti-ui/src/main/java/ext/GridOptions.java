package ext;

import java.time.LocalDateTime;
import java.util.List;

import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import ji.common.structures.SortedMap;
import toti.validation.ItemRules;
import toti.validation.Validator;

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
                     ItemRules.forName(column.getName(), false).setType(type)
                     .setChangeValue((v)->{
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
					ItemRules.forName(column.getName(), false).setType(boolean.class)
					//setAllowedValues(Arrays.asList("DESC", "ASC"))
					.setChangeValue((v)->{
						if (v == null) {
							return null;
						}
						return new Sort(column.getSortingName(), (boolean)v);
					})
				);
			}
		});
		return new Validator(true)
			.addRule(ItemRules.forName("pageIndex", true).setType(Integer.class))
			.addRule(ItemRules.forName("pageSize", true).setType(Integer.class))
			.addRule(ItemRules.forName("filters", true).setSortedMapSpecification(filters))
			.addRule(ItemRules.forName("sorting", true).setSortedMapSpecification(sorting));
	}

}
