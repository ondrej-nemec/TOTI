package ji.querybuilder.builders.parents;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
import ji.database.support.DatabaseRow;

public interface Fetch {

	DictionaryValue fetchSingle() throws SQLException;
	
	DatabaseRow fetchRow() throws SQLException;
	
	default <T> T fetchRow(Class<T> clazz) throws SQLException {
		DatabaseRow row = fetchRow();
		if (row == null) {
			return null;
		}
		return row.parse(clazz);
	}
	
	default <T> T fetchRow(ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException {
		DatabaseRow row = fetchRow();
		if (row == null) {
			 return null;
		}
		return function.apply(row);
	}
	
	default List<DatabaseRow> fetchAll() throws SQLException {
		return fetchAll((r)->r);
	}
	
	<T> List<T> fetchAll(ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException;
	
	<K, V> Map<K, V> fetchAll(
		ThrowingFunction<DatabaseRow, K, SQLException> key,
		ThrowingFunction<DatabaseRow, V, SQLException> value
	) throws SQLException;
	
}
