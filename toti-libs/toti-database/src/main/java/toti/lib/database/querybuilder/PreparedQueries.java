package toti.lib.database.querybuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.lib.database.base.support.DatabaseRow;
import toti.lib.database.querybuilder.builders.InsertBuilder;
import toti.lib.database.querybuilder.builders.SelectBuilder;
import toti.lib.database.querybuilder.builders.UpdateBuilder;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ThrowingBiConsumer;
import toti.lib.common.structures.ThrowingConsumer;
import toti.lib.common.structures.ThrowingFunction;
import toti.lib.common.structures.ThrowingSupplier;

public interface PreparedQueries extends QueryBuilderFactory {
	
	default String[] _createSelect(String... select) {
		if (select == null || select.length == 0) {
			return new String[] {"*"};
		}
		return select;
	}
	
	/******/
	
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
		return select(_createSelect(select)).from(table)
				.where(idName + " = :id").addParameter(":id", id)
				.fetchRow();
	}
	
	/******/
	
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
	
	/******/
	
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
	
	/******/
	
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
	
	/******/
	
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
	
}
