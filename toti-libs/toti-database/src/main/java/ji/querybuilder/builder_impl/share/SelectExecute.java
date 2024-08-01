package ji.querybuilder.builder_impl.share;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
import ji.database.support.DatabaseRow;
import ji.database.wrappers.StatementWrapper;

public interface SelectExecute {

	/** INTERNAL */
	default <T> T _execute(Connection connection, String query, Map<String, String> parameters,
			ThrowingFunction<ResultSet, T, SQLException> callback) throws SQLException {
		try (Statement stat = connection.createStatement(); ResultSet res = stat.executeQuery(query);) {
			if (stat instanceof StatementWrapper) {
				StatementWrapper w = StatementWrapper.class.cast(stat);
				w.getProfiler().builderQuery(w.ID, query, query, parameters);
			}
			return callback.apply(res);
		}
	}

	/** INTERNAL */
	default DatabaseRow _parseRow(ResultSet res) throws SQLException {
		DatabaseRow row = new DatabaseRow();
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
			row.addValue(res.getMetaData().getColumnLabel(i), _parseValue(res, i));
		}
		return row;
	}
	
	/** INTERNAL */
	default Object _parseValue(ResultSet rs, int index) throws SQLException {
		Object value = rs.getObject(index);
		if (value instanceof Date) {
			return LocalDate.parse(value.toString());
		}
		if (value instanceof Time) {
			return LocalTime.parse(value.toString());
		}
		if (value instanceof Timestamp) {
			String text = rs.getString(index);
			text = text.replaceFirst(" ", "T").replaceFirst(" ", "");
			if (text.contains("+") || StringUtils.countMatches(text, "-") > 3) {
				if (StringUtils.countMatches(text, ":") == 2) {
					text += ":00";
				}
				return ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);
			} else {
				return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
			}
		}
		return value;
	}
	
	/************/
	
	default DictionaryValue fetchSingle(Connection connection, String query, Map<String, String> parameters) throws SQLException {
		return _execute(connection, query, parameters, (rs)->{
			if (rs.next()) {
				return new DictionaryValue(_parseValue(rs, 1));
			}
			return null;
		});
	}

	default DatabaseRow fetchRow(Connection connection, String query, Map<String, String> parameters) throws SQLException {
		return _execute(connection, query, parameters, (res)->{
			if (res.next()) {
				return _parseRow(res);
			}
			return null;
		});
	}
/*
	default <T> T fetchRow(Connection connection, String query, Map<String, String> parameters, Class<T> clazz) throws SQLException {
		DatabaseRow row = fetchRow(connection, query, parameters);
		if (row == null) {
			return null;
		}
		return row.parse(clazz);
	}

	default <T> T fetchRow(Connection connection, String query, Map<String, String> parameters,
			ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException {
		DatabaseRow row = fetchRow(connection, query, parameters);
		if (row == null) {
			 return null;
		}
		return function.apply(row);
	}

	default List<DatabaseRow> fetchAll(Connection connection, String query, Map<String, String> parameters) throws SQLException {
		return fetchAll(connection, query, parameters, (r)->r);
	}
*/
	default <T> List<T> fetchAll(Connection connection, String query, Map<String, String> parameters,
			ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException {
		return _execute(connection, query, parameters, (res)->{
			List<T> rows = new LinkedList<>();
			while (res.next()) {
				rows.add(function.apply(_parseRow(res)));
			}
			return rows;
		});
	}

	default <K, V> Map<K, V> fetchAll(
			Connection connection, String query, Map<String, String> parameters,
			ThrowingFunction<DatabaseRow, K, SQLException> key,
			ThrowingFunction<DatabaseRow, V, SQLException> value) throws SQLException {
		return _execute(connection, query, parameters, (res)->{
			Map<K, V> rows = new HashMap<>();
			while (res.next()) {
				DatabaseRow row = _parseRow(res);
				rows.put(key.apply(row), value.apply(row));
			}
			return rows;
		});
	}
	
}
