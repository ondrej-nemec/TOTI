package toti.application;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.translator.Translator;

public interface GridEntityDao<T extends Entity> {
	
	Database getDatabase();
	
	String getTableName();
	
	T createEntity(DatabaseRow row, Translator translator);
/*
	default SelectBuilder _getAll(String select, QueryBuilder builder) {
		return builder.select(select).from(getTableName());
	}
*/
	SelectBuilder _getGrid(String select, QueryBuilder builder);
	
	default Optional<String> getOwnerColumnName() {
		return Optional.empty();
	}

	default GridDataSet<T> getAll(GridOptions options, Collection<Object> forOwners, Translator translator) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			SelectBuilder count = _getGrid("count(*)", builder);
			_applyFilters(builder, count, options.getFilters(), forOwners);
			
			SelectBuilder select = _getGrid("*", builder);
			_applyFilters(builder, select, options.getFilters(), forOwners);
			_applySorting(select, options.getSorting());
			
			int countOfResults = count.fetchSingle().getInteger();
			GridRange range = GridRange.create(countOfResults, options.getPageIndex(), options.getPageSize());
			if (range.isPresent()) {
				select.limit(range.getLimit(), range.getOffset());
			}
			List<T> items = new LinkedList<>();
			select.fetchAll().forEach((row)->{
				items.add(createEntity(row, translator));
			});
			return new GridDataSet<>(items, countOfResults, range.getPageIndex());
		});
	}
	
	default void _applySorting(SelectBuilder select, Map<String, Sort> sorting) {
		StringBuilder orderBY = new StringBuilder();
		sorting.forEach((sortName, sort)->{
			if (!orderBY.toString().isEmpty()) {
				orderBY.append(", ");
			}
			orderBY.append(sort.getName() + " " + (sort.isDesc() ? "DESC" : "ASC"));
		});
		if (!sorting.isEmpty()) {
			select.orderBy(orderBY.toString());
		}
	}
	
	default void _applyFilters(
			QueryBuilder builder,
			SelectBuilder select,
			Map<String, Filter> filters,
			Collection<Object> forOwners) {
		select.where("1=1");
		if (getOwnerColumnName().isPresent()) {
			if (!forOwners.isEmpty()) {
				select.andWhere(getOwnerColumnName().get() + " in (:in)")
					.addParameter(":in", forOwners);
			} else {
				select.andWhere("1=2"); // no results
			}
		}
		select.addParameter(":empty", "");
		filters.forEach((filterName, filter)->{
			String where = "";
			if (filter.getValue() == null) {
				where = filter.getName() + " is null";
			} else {
				where = filter.getName() + " " + (filter.getMode() == FilterMode.EQUALS ? "=" : "LIKE")
					+ " :" + filter.getName() + "Value";
			}
			/*else if (value.toString().length() > 20) {
				where = builder.getSqlFunctions().concat(":empty", filter)
						+ " like :" + filter + "LikeValue"
						+ " OR " + filter + " = :" + filter + "Value";
			} else {
				// this is fix for derby DB - integer cannot be concat or casted to varchar only on char
				where = builder.getSqlFunctions().cast(filter, ColumnType.charType(20))
						+ " like :" + filter + "LikeValue"
						+ " OR " + filter + " = :" + filter + "Value";
			}*/
			select.andWhere(where)
			.addParameter(
				":" + filter.getName() + "Value", 
				String.format(filter.getMode().getFormat(), filter.getValue())
			);
		});
	}
	
	/****************/
	
	static final String HELP_KEY_NAME = "help_key";
	static final String HELP_DISPLAY_VALUE_NAME = "help_display_value";
	static final String HELP_DISABLED_NAME = "help_disabled";
	static final String HELP_GROUP_NAME = "help_group";
	
	default String getHelpKey() {
		return null;
	}
	
	default String getHelpDisplayValue() {
		return null;
	}
	
	default String getHelpDisabled() {
		return null;
	}
	
	default String getHelpOptgroup() {
		return null;
	}
	
	default SelectBuilder _getHelp(QueryBuilder builder) {
		StringBuilder select = new StringBuilder();
		select.append(getHelpKey());
		select.append(" AS ");
		select.append(HELP_KEY_NAME);
		select.append(",");
		
		select.append(getHelpDisplayValue());
		select.append(" AS ");
		select.append(HELP_DISPLAY_VALUE_NAME);
		if (getHelpDisabled() != null) {
			select.append(", ");
			select.append(getHelpDisabled());
			select.append(" AS ");
			select.append(HELP_DISABLED_NAME);
		}
		if (getHelpOptgroup() != null) {
			select.append(", ");
			select.append(getHelpOptgroup());
			select.append(" AS ");
			select.append(HELP_GROUP_NAME);
		}
		return builder.select(select.toString()).from(getTableName()).orderBy(getHelpDisplayValue());
	}
	
	default List<Help> getHelp(Collection<Object> forOwners) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			List<Help>items = new LinkedList<>();
			SelectBuilder select = _getHelp(builder);
			if (getOwnerColumnName().isPresent()) {
				if (!forOwners.isEmpty()) {
					select.where(getOwnerColumnName().get() + " in (:in)").addParameter(":in", forOwners);
				} else {
					select.where("1=2"); // no results
				}
			}
			select.fetchAll().forEach((row)->{
				items.add(
					new Help(
						row.getValue(HELP_KEY_NAME), 
						row.getValue(HELP_DISPLAY_VALUE_NAME),
						row.getString(HELP_GROUP_NAME),
						row.getValue(HELP_DISABLED_NAME) == null ? false : row.getBoolean(HELP_DISABLED_NAME)
					)
				);
			});
			return items;
		});
	}

}
