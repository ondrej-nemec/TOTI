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
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.builders.UpdateBuilder;
import ji.querybuilder.enums.ColumnType;

public interface EntityDao<T extends Entity> {
	
	Database getDatabase();
	
	String getTableName();
	
	T createEntity(DatabaseRow row);
	
	default Optional<String> getOwnerColumnName() {
		return Optional.empty();
	}
	
	default SelectBuilder _getAll(String select, QueryBuilder builder) {
		return builder.select(select).from(getTableName());
	}
	
	default List<T> getAll() throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return _getAll("*", builder)
			.fetchAll((row)->{
				return createEntity(row);
			});
		});
	}
	
	default GridDataSet<T> getAll(GridOptions options, Collection<Object> forOwners) throws SQLException {
        return getAll(
             options.getPageIndex(),
             options.getPageSize(),
             options.getFilters(),
             options.getSorting(),
             forOwners
        );
    }

    @Deprecated
	default GridDataSet<T> getAll(
			int indexOfPage,
			int pageSize, 
			Map<String, Object> filters,
			Map<String, Object> sorting,
			Collection<Object> forOwners) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			SelectBuilder count = _getAll("count(*)", builder);
			_applyFilters(builder, count, filters, forOwners);
			
			SelectBuilder select = _getAll("*", builder);
			_applyFilters(builder, select, filters, forOwners);
			StringBuilder orderBY = new StringBuilder();
			sorting.forEach((sort, direction)->{
				if (!orderBY.toString().isEmpty()) {
					orderBY.append(", ");
				}
				orderBY.append(sort + " " + direction);
			});
			if (!sorting.isEmpty()) {
				select.orderBy(orderBY.toString());
			}
			
			int countOfResults = count.fetchSingle().getInteger();
			GridRange range = GridRange.create(countOfResults, indexOfPage, pageSize);
			
			select.limit(range.getLimit(), range.getOffset());
			List<T> items = new LinkedList<>();
			select.fetchAll().forEach((row)->{
				items.add(createEntity(row));
			});
			return new GridDataSet<>(items, countOfResults, range.getPageIndex());
		});
	}
	
	default void _applyFilters(
			QueryBuilder builder,
			SelectBuilder select,
			Map<String, Object> filters,
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
		filters.forEach((filter, value)->{
			String where = "";
			if (value == null) {
				where = filter + " is null";
			} else if (value.toString().length() > 20) {
				where = builder.getSqlFunctions().concat(":empty", filter)
						+ " like :" + filter + "LikeValue"
						+ " OR " + filter + " = :" + filter + "Value";
			} else {
				// this is fix for derby DB - integer cannot be concat or casted to varchar only on char
				where = builder.getSqlFunctions().cast(filter, ColumnType.charType(20))
						+ " like :" + filter + "LikeValue"
						+ " OR " + filter + " = :" + filter + "Value";
			}
			select.andWhere(where)
			.addParameter(":" + filter + "LikeValue", value + "%")
			.addParameter(":" + filter + "Value", value);
		});
	}
	
	default SelectBuilder _get(QueryBuilder builder, int id) {
		return builder.select("*").from(getTableName())
				.where("id = :id").addParameter(":id", id);
	}
	
	default T get(int id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return createEntity(_get(builder, id).fetchRow());
		});
	}
	
	default T delete(int id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			T item = get(id);
			builder.delete(getTableName()).where("id = :id").addParameter(":id", id).execute();
			return item;
		});
	}
	
	default int _update(QueryBuilder builder, int id, T entity) throws SQLException {
		UpdateBuilder b = builder.update(getTableName());
		entity.toMap().forEach((name, value)->{
			b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
		});
		b.where("id = :id").addParameter(":id", id);
		return b.execute();
	}
	
	default int update(int id, T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return _update(builder, id, entity);
		});
	}
	
	default int _insert(QueryBuilder builder, T entity) throws SQLException {
		InsertBuilder b = builder.insert(getTableName());
		entity.toMap().forEach((name, value)->{
			b.addValue(name, value);
		});
		return Integer.parseInt(b.execute().toString());
	}
	
	default int insert(T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return _insert(builder, entity);
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
