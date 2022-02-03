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
	private Map<String, Object> filters = new HashMap<>();
	@MapperParameter({@MapperType("sorting")})
	private Map<String, Object> sorting = new HashMap<>();
	
	public Integer getPageIndex() {
		return pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public Map<String, Object> getFilters() {
		return filters;
	}
	public Map<String, Object> getSorting() {
		return sorting;
	}
	
	public static Validator getValidator(List<GridColumn> gridColumns) {
		Validator filters = new Validator(true);
		Validator sorting = new Validator(true);
		gridColumns.forEach((column)->{
			if (column.isUseInFilter()) {
				filters.addRule(
					ItemRules.forName(column.getName(), false).setType(column.getType())
				);
			}
			if (column.isUseInSorting()) {
				sorting.addRule(
					ItemRules.forName(column.getName(), false).setAllowedValues(Arrays.asList("DESC", "ASC"))
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
