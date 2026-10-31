package toti.lib.database.querybuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ThrowingBiConsumer;
import toti.lib.common.structures.ThrowingConsumer;
import toti.lib.common.structures.ThrowingFunction;
import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.database.base.support.DatabaseRow;
import toti.lib.database.querybuilder.builders.InsertBuilder;
import toti.lib.database.querybuilder.builders.SelectBuilder;
import toti.lib.database.querybuilder.builders.UpdateBuilder;
import toti.lib.database.querybuilder.structures.preparedQueries.Filter;
import toti.lib.database.querybuilder.structures.preparedQueries.Sort;

public interface PreparedQueries extends QueryBuilderFactory {
	
	/********************** GET *****************************/
	
	/**
	 * method returns one DatabaseRow (or null) in given table by given column.
	 * @param table String - source table
	 * @param idName String - column name to search in
	 * @param id Object - value to search for
	 * @param select - columns, can be empty
	 * @return DatabaseRow or NULL
	 * @throws SQLException
	 */
	default DatabaseRow get(String table, String idName, Object id, String... select) throws SQLException {
		return get(table, idName, id, r->r, select);
	}

	/**
	 * method returns one T (or null) in given table by given column.
	 * @param table String - source table
	 * @param idName String - column name to search in
	 * @param id Object - value to search for
	 * @param create ThrowingFunction<DatabaseRow, T, SQLException> function used to create new T if row exists
	 * @param select - columns, can be empty
	 * @return DatabaseRow or NULL
	 * @throws SQLException
	 */
	default <T> T get(String table, String idName, Object id, ThrowingFunction<DatabaseRow, T, SQLException> create, String... select) throws SQLException {
		DatabaseRow row = select(_createSelect(select)).from(table)
				.where(idName + " = :id").addParameter(":id", id)
				.fetchRow();
		if (row == null) {
			return null;
		}
		return create.apply(row);
	}
	
	/********************** EXISTS *****************************/

	/**
	 * Check if exists row with given id
	 * @param table to look in
	 * @param idName name of unique column
	 * @param id
	 * @return true if there is row with given id, false otherwise
	 * @throws SQLException
	 */
	default boolean exists(String table, String idName, Object id) throws SQLException {
		return get(table, idName, id, idName) != null; // select only name of id
	}

	/**
	 * Check if exists another row with given params
	 * @param table to look in
	 * @param idName name of unique column
	 * @param id to exclude
	 * @param params
	 * @return true if there is row with given params, false otherwise
	 * @throws SQLException
	 */
	default boolean exists(String table, String idName, Object id, Map<String, Object> params) throws SQLException {
		SelectBuilder select = select(idName).from(table);
		if (id != null) {
			select.where(idName + " != :" + idName)
				.addParameter(":" + idName, id);
		}
		params.forEach((name, value)->{
			select.where(name + " = :" + name)
				.addParameter(":" + name, value);
		});
		return select.fetchRow() != null;
	}
	
	/********************** GET ALL *****************************/
	
	/**
	 * returns all rows in table
	 * @param table - source table
	 * @param select - columns, can be empty
	 * @return List of DatabaseRow
	 * @throws SQLException
	 */
	default List<DatabaseRow> getAll(String table, String... select) throws SQLException {
		return getAll(table, r->r, select);
	}
	
	/**
	 * returns all rows in table
	 * @param <T>
	 * @param table - source table
	 * @param create - parse DatabaseRow to T
	 * @param select - columns, can be empty
	 * @return List of T
	 * @throws SQLException
	 */
	default <T> List<T> getAll(String table, ThrowingFunction<DatabaseRow, T, SQLException> create, String... select) throws SQLException {
		return _getAll(table, select).fetchAll(create);
	}
	
	default SelectBuilder _getAll(String table, String... select) {
		 return select(_createSelect(select)).from(table);
	}
	
	/**
	 * returns all items by specific column
	 * @param table - source table
	 * @param idName - column name to search in
	 * @param id - value to search for
	 * @param select - columns, can be empty
	 * @return List of DatabaseRow
	 * @throws SQLException
	 */
	default List<DatabaseRow> getAllBy(String table, String idName, Object id, String... select) throws SQLException {
		return getAllBy(table, idName, id, r->r, select);
	}
	
	/**
	 * returns all items by specific column
	 * @param <T>
	 * @param table - source table
	 * @param idName - column name to search in
	 * @param id - value to search for
	 * @param create - parse DatabaseRow to T
	 * @param select - columns, can be empty
	 * @return List of T
	 * @throws SQLException
	 */
	default <T> List<T> getAllBy(String table, String idName, Object id, ThrowingFunction<DatabaseRow, T, SQLException> create, String... select) throws SQLException {
		return select(_createSelect(select)).from(table)
			.where(idName + " = :id").addParameter(":id", id)
			.fetchAll(create);
	}
	
	/**
	 * returns all items by specific column
	 * @param table - source table
	 * @param idName - column name to search in
	 * @param ids - values to search for
	 * @param select - columns, can be empty
	 * @return List of DatabaseRow
	 * @throws SQLException
	 */
	default List<DatabaseRow> getAllIn(String table, String idName, Collection<?> ids, String... select) throws SQLException {
		return getAllIn(table, idName, ids, r->r, select);
	}
	
	/**
	 * returns all items by specific column
	 * @param <T>
	 * @param table - source table
	 * @param idName - column name to search in
	 * @param ids - values to search for
	 * @param create - parse DatabaseRow to T
	 * @param select - columns, can be empty
	 * @return List of T
	 * @throws SQLException
	 */
	default <T> List<T> getAllIn(String table, String idName, Collection<?> ids, ThrowingFunction<DatabaseRow, T, SQLException> create, String... select) throws SQLException {
		return select(_createSelect(select)).from(table)
				.where(idName + " in (:id)").addParameter(":id", ids)
				.fetchAll(create);
	}

	default void applySelect(
		SelectBuilder select, List<Filter> filters, List<Sort> sorting, Integer offset, Integer limit
	) throws SQLException {
		_applyFiltering(select, filters);
		_applySorting(select, sorting);
		if (limit != null) {
			if (offset != null) {
				select.limit(limit, offset);
			} else {
				select.limit(limit);
			}
		}
	}

	/********************** MODIFY *****************************/
	
	/**
	 * delete rows with speficied column
	 * @param table - source table
	 * @param idName - column name to delete by
	 * @param id - value to delete by
	 * @return count of affected rows
	 * @throws SQLException
	 */
	default int delete(String table, String idName, Object id) throws SQLException {
		return delete(table).where(idName + " = :id").addParameter(":id", id).execute();
	}
	
	/**
	 * update rows specified by column
	 * @param table - source table
	 * @param idName - column name to update by
	 * @param id - value to update by
	 * @param data
	 * @return count of affected rows
	 * @throws SQLException
	 */
	default int update(String table, String idName, Object id, Map<String, Object> data) throws SQLException {
		UpdateBuilder b = update(table);
		data.forEach((name, value)->{
			b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
		});
		b.where(idName + " = :id").addParameter(":id", id);
		return b.execute();
	}
	
	/**
	 * insert one new row 
	 * @param table - source table
	 * @param data
	 * @return id as DictionaryValue
	 * @throws SQLException
	 */
	default DictionaryValue insert(String table, Map<String, Object> data) throws SQLException {
		InsertBuilder b = insert(table);
		data.forEach((name, value)->{
			b.addValue(name, value);
		});
		return b.execute();
	}
	
	/********************** SAVE RELATION *****************************/
	
	/**
	 * update/create/delete rows in related table
	 * @param <T>
	 * @param relationTable - table to changes
	 * @param primaryKeyName - column name of unique column (PrimaryKey)
	 * @param foreignKeyName - column name of column related to another table (Foreigh Key)
	 * @param id - value common for all rows to change (Foreigh Key)
	 * @param data
	 * @throws SQLException
	 */
	default <T> void saveRelation(
			String relationTable,
			String primaryKeyName, String foreignKeyName,
			Object id, List<DatabaseRow> data) throws SQLException {
		saveRelation(
			data,
			row->row.getValue(primaryKeyName),
			()->getAllBy( relationTable, foreignKeyName, id, primaryKeyName),
			row->delete(relationTable, primaryKeyName, row.getValue(primaryKeyName)),
			(origin, newOne)->
				update(relationTable, primaryKeyName, origin.getValue(primaryKeyName), newOne.toMap()),
			row->{
				row.put(foreignKeyName, id);
				insert(relationTable, row.toMap());
			}
		);
	}
	
	default <T> void saveRelation(Collection<T> data, Function<T, Object> createId,
			ThrowingSupplier<List<T>, SQLException> select,
			ThrowingConsumer<T, SQLException> delete,
			ThrowingBiConsumer<T, T, SQLException> update,
			ThrowingConsumer<T, SQLException> insert) throws SQLException {
		Map<Object, T> loaded = new HashMap<>();
		List<T> forInsert = new LinkedList<>();
		for (T t : data) {
			Object id = createId.apply(t);
			if (id == null) {
				forInsert.add(t);
			} else {
				loaded.put(id, t);
			}
		}
		List<T> database = select.get();
		for (T origin : database) {
			if (!loaded.containsKey(createId.apply(origin))) {
				delete.accept(origin);
			} else {
				T l = loaded.remove(createId.apply(origin));
				if (!l.equals(origin)) {
					update.accept(origin, l);
				}
			}
		}
		forInsert.addAll(loaded.values());
		for (T toInsert : forInsert) {
			 insert.accept(toInsert);
		}
	}
	
	/********************** INTERNAL *****************************/
	
	default String[] _createSelect(String... select) {
		if (select == null || select.length == 0) {
			return new String[] {"*"};
		}
		return select;
	}

	default void _applySorting(SelectBuilder select, List<Sort> sorting) {
		StringBuilder orderBY = new StringBuilder();
		sorting.forEach(sort->{
			if (!orderBY.toString().isEmpty()) {
				orderBY.append(", ");
			}
			orderBY
			.append(sort.name())
			.append(" ")
			.append(sort.isDesc() ? "DESC" : "ASC");
		});
		if (!sorting.isEmpty()) {
			select.orderBy(orderBY.toString());
		}
	}

	default void _applyFiltering(SelectBuilder select, List<Filter> filters) {
		select.addParameter(":empty", "");
		filters.forEach((filter)->{
			select.where(filter.create(getSqlFunctions()))
			.addParameter(":" + filter.getName() + "Value", filter.getValue());
		});
	}

}
