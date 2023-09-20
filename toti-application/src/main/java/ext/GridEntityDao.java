package ext;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.common.structures.SortedMap;
import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.translator.Translator;

public interface GridEntityDao<T extends Entity>{
	
	Database getDatabase();
	
	String getTableName();
	
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
	
	default String getHelpTable() {
		return getTableName();
	}
	
	default Object onRow(DatabaseRow row, Translator translator) {
		return row;
	}

	SelectBuilder _getGrid(String select, QueryBuilder builder);
	
	default Optional<String> getOwnerColumnName() {
		return Optional.empty();
	}

	default GridDataSet getAll(GridOptions options, Collection<Object> forOwners, Translator translator) throws SQLException {
		return getAll(
			getDatabase(), options, forOwners,
			(select, builder)->_getGrid(select, builder),
			row->onRow(row, translator),
			getOwnerColumnName()
		);
	}
	
	default List<Help> getHelp(Collection<Object> forOwners) throws SQLException {
		return getHelp(
			getDatabase(), getHelpTable(), forOwners, getOwnerColumnName(),
			getHelpKey(), getHelpDisplayValue(), getHelpDisabled(), getHelpOptgroup()
		);
	}
	
	
	/*************/

	default GridDataSet getAll(
			Database database, GridOptions options,
			Collection<Object> forOwners, BiFunction<String, QueryBuilder, SelectBuilder> getSelect,
			Function<DatabaseRow, Object> create, Optional<String> ownerColumnName) throws SQLException {
		return database.applyBuilder((builder)->{
			SelectBuilder count = getSelect.apply("count(*)", builder);
			_applyFilters(builder, count, options.getFilters(), forOwners, ownerColumnName);
			
			SelectBuilder select = getSelect.apply("*", builder);
			_applyFilters(builder, select, options.getFilters(), forOwners, ownerColumnName);
			_applySorting(select, options.getSorting());
			
			int countOfResults = count.fetchSingle().getInteger();
			GridRange range = GridRange.create(countOfResults, options.getPageIndex(), options.getPageSize());
			if (range.isPresent()) {
				select.limit(range.getLimit(), range.getOffset());
			}
			List<Object> items = new LinkedList<>();
			select.fetchAll().forEach((row)->{
				items.add(create.apply(row));
			});
			return new GridDataSet(items, countOfResults, range.getPageIndex());
		});
	}
	
	default void _applySorting(SelectBuilder select, SortedMap<String, Sort> sorting) {
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
			SortedMap<String, Filter> filters,
			Collection<Object> forOwners,
			Optional<String> ownerColumnName) {
		select.where("1=1");
		if (ownerColumnName.isPresent()) {
			if (!forOwners.isEmpty()) {
				select.andWhere(ownerColumnName.get() + " in (:in)")
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
            } else if (filter.getMode().getOperand() != null) {
                where = filter.getName() + " " + filter.getMode().getOperand() + " :" + filter.getName() + "Value";
            } else if (filter.getMode() == FilterMode.EQUALS) {
                where = compare(filter.getName(), filter) + " = " + compare(":" + filter.getName() + "Value", filter);
            // v2
            } else {
                where = compare(builder.getSqlFunctions().concat(":empty", filter.getName()), filter)
                         + " LIKE " + compare(":" + filter.getName() + "Value", filter);
            }
			// v1
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
	
	default String compare(String value, Filter filter) {
        // TODO use sql func. class
        // TODO use sql func for collate latin
        // WHERE Name COLLATE Latin1_general_CI_AI Like '%cafe%' COLLATE Latin1_general_CI_AI
        // latin1_general_cs
        /*if (filter.isCI() && filter.isIgnoreDiacritics()) {
             return String.format("unacce", value);
        }
        if (filter.isIgnoreDiacritics()) {
             return String.format("%s COLLATE latin1_general_cs", value);
        }*/

        if (filter.isCI()) {
             return String.format("lower(%s)", value);
        }
        return value;

    }

	static final String HELP_KEY_NAME = "help_key";
	static final String HELP_DISPLAY_VALUE_NAME = "help_display_";
	static final String HELP_DISABLED_NAME = "help_disabled_";
	static final String HELP_GROUP_NAME = "help_group_";
	
	default List<Help> getHelp(
			Database database, String table,
			Collection<Object> forOwners, Optional<String> ownerColumnName, 
			String key, String title, String disabled, String optGroup) throws SQLException {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(key);
		selectQuery.append(" AS ");
		selectQuery.append(HELP_KEY_NAME);
		selectQuery.append(",");             

		selectQuery.append(title);
		selectQuery.append(" AS ");
		selectQuery.append(HELP_DISPLAY_VALUE_NAME);
		if (disabled != null) {
		    selectQuery.append(", ");
		    selectQuery.append(disabled);
		    selectQuery.append(" AS ");
		    selectQuery.append(HELP_DISABLED_NAME);
		}
		StringBuilder sorting = new StringBuilder();
		if (optGroup != null) {
			sorting.append(optGroup);
		    selectQuery.append(", ");
		    selectQuery.append(optGroup);
		    selectQuery.append(" AS ");
		    selectQuery.append(HELP_GROUP_NAME);
		}
		if (!sorting.toString().isEmpty()) {
			sorting.append(",");
		}
		sorting.append(title);
		return getHelp(
			database, 
			builder->builder.select(selectQuery.toString()).from(table), 
			forOwners, ownerColumnName, sorting.toString()
		);
	}
	
	default List<Help> getHelp(
			Database database, Function<QueryBuilder, SelectBuilder> selectFactory,
			Collection<Object> forOwners, Optional<String> ownerColumnName, 
			String sortingColumn) throws SQLException {
		return database.applyBuilder((builder)->{
			List<Help>items = new LinkedList<>();
		    SelectBuilder select = selectFactory.apply(builder);
		    if (ownerColumnName.isPresent() && forOwners != null) {
		        if (!forOwners.isEmpty()) {
		             select.where(ownerColumnName.get() + " in (:in)").addParameter(":in", forOwners);
		        } else {
		             select.where("1=2"); // no results
		        }
		    }
		    select.orderBy(sortingColumn);
		    
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