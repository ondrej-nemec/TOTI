package toti.application;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import database.Database;
import database.support.DatabaseRow;
import querybuilder.QueryBuilder;
import querybuilder.builders.InsertBuilder;
import querybuilder.builders.SelectBuilder;
import querybuilder.builders.UpdateBuilder;
import querybuilder.enums.ColumnType;

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
			if (countOfResults == 0) {
				return new GridDataSet<>(new LinkedList<>(), countOfResults, 1);
			}
			int pageIndex = indexOfPage;
			if (pageIndex * pageSize > countOfResults) {
				pageIndex = 1;
			}
			select.limit(Math.min(pageIndex*pageSize, countOfResults), (pageIndex-1)*pageSize);
			List<T> items = new LinkedList<>();
			select.fetchAll().forEach((row)->{
				items.add(createEntity(row));
			});
			return new GridDataSet<>(items, countOfResults, pageIndex);
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
	
	default int update(int id, T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			UpdateBuilder b = builder.update(getTableName());
			entity.toMap().forEach((name, value)->{
				b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
			});
			b.where("id = :id").addParameter(":id", id);
			return b.execute();
		});
	}
	
	default int insert(T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			InsertBuilder b = builder.insert(getTableName());
			entity.toMap().forEach((name, value)->{
				b.addValue(name, value);
			});
			return Integer.parseInt(b.execute().toString());
		});
	}
	
	/****************/
	
	static final String HELP_KEY_NAME = "_help_key";
	static final String HELP_DISPLAY_VALUE_NAME = "_help_display_value";
	static final String HELP_DISABLED_NAME = "_help_disabled";
	static final String HELP_GROUP_NAME = "_help_group";
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
		return builder.select(select.toString()).from(getTableName());
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
