package toti.application;

import java.sql.SQLException;
import java.util.List;

import ji.common.structures.DictionaryValue;
import ji.database.Database;
import ji.database.support.DatabaseRow;

public interface EntityDao<T extends Entity> {
	
	Database getDatabase();
	
	String getTableName();
	
	T createEntity(DatabaseRow row);

	default String getIdName() {
		return "id";
	}

	default List<T> getAll() throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return builder.getAll(builder, getTableName(), row->createEntity(row));
		});
	}

	default T get(Object id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return createEntity(builder.get(builder, getTableName(), getIdName(), id));
		});
	}
	
	default T delete(Object id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			T item = get(id);
			builder.delete(builder, getTableName(), getIdName(), id);
			return item;
		});
	}

	default int update(Object id, T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return builder.update(builder, getTableName(), getIdName(), id, entity.toMap());
		});
	}

	default DictionaryValue insert(T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return builder.insert(builder, getTableName(), entity.toMap());
		});
	}
	
}
