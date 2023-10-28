package toti.ui.backend.grid;

import java.sql.SQLException;
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
import toti.ui.backend.Owner;

public interface GridDao {
	
	default Object onGridRow(DatabaseRow row, Translator translator) {
		return row;
	}

	SelectBuilder _getGrid(String select, QueryBuilder builder);

	default GridDataSet getAll(Database database, String table, GridOptions options) throws SQLException {
		return getAll(database, table, options, Optional.empty(), null);
	}

	default GridDataSet getAll(Database database, String table, GridOptions options, Translator translator) throws SQLException {
		return getAll(database, table, options, Optional.empty(), translator);
	}

	default GridDataSet getAll(
			Database database, String table, GridOptions options,
			Optional<Owner> owner) throws SQLException {
		return getAll(database, table, options, owner, null);
	}

	default GridDataSet getAll(
			Database database, String table, GridOptions options,
			Optional<Owner> owner, Translator translator) throws SQLException {
		return getAll(
			database, options, owner,
			(select, builder)->_getGrid(select, builder),
			row->onGridRow(row, translator)
		);
	}
	
	/*************/

	default GridDataSet getAll(
			Database database, GridOptions options,
			Optional<Owner> owner, BiFunction<String, QueryBuilder, SelectBuilder> getSelect,
			Function<DatabaseRow, Object> create) throws SQLException {
		return database.applyBuilder((builder)->{
			SelectBuilder count = getSelect.apply("count(*)", builder);
			_applyFilters(builder, count, options.getFilters(), owner);
			
			SelectBuilder select = getSelect.apply("*", builder);
			_applyFilters(builder, select, options.getFilters(), owner);
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
			Optional<Owner> owner) {
		select.where("1=1");
		if (owner.isPresent()) {
			Owner o = owner.get();
			if (!o.getAllowedValues().isEmpty()) {
				select.andWhere(o.getColumnName() + " in (:in)")
					.addParameter(":in", o.getAllowedValues());
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
           //   result = String.format("unaccent(%s)", result);
             return String.format("%s COLLATE latin1_general_cs", value);
        }*/

        if (filter.isCI()) {
        	// result = String.format("lower(%s)", result);
            return String.format("lower(%s)", value);
        }
        return value;

    }
}