package toti.application;

import java.sql.SQLException;
import java.util.List;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
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
		return getAll(getDatabase(), getTableName(), row->createEntity(row));
		/*return getDatabase().applyBuilder((builder)->{
			return builder.getAll(builder, getTableName(), row->createEntity(row));
		});*/
	}

	default T get(Object id) throws SQLException {
		return get(getDatabase(), getTableName(), getIdName(), id, row->createEntity(row));
		/*return getDatabase().applyBuilder((builder)->{
			return createEntity(builder.get(builder, getTableName(), getIdName(), id));
		});*/
	}
	
	default T delete(Object id) throws SQLException {
		return delete(getDatabase(), getTableName(), getIdName(), id, row->createEntity(row));
		/*return getDatabase().applyBuilder((builder)->{
			T item = get(id);
			builder.delete(builder, getTableName(), getIdName(), id);
			return item;
		});*/
	}

	default int update(Object id, T entity) throws SQLException {
		return update(getDatabase(), getTableName(), getIdName(), id, entity);
		/*return getDatabase().applyBuilder((builder)->{
			return builder.update(builder, getTableName(), getIdName(), id, entity.toMap());
		});*/
	}

	default DictionaryValue insert(T entity) throws SQLException {
		return insert(getDatabase(), getTableName(), entity);
		/*return getDatabase().applyBuilder((builder)->{
			return builder.insert(builder, getTableName(), entity.toMap());
		});*/
	}
	
	/********************************/

	default <S extends Entity> List<S> getAll(Database database, String table, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.getAll(table, create);
		});
	}

	default <S extends Entity> S get(Database database, String table, String idName, Object id, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return database.applyBuilder((builder)->{
			return create.apply(builder.get(table, idName, id));
		});
	}
	
	default <S extends Entity> S delete(Database database, String table, String idName, Object id, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return database.applyBuilder((builder)->{
			S item = get(database, table, idName, id, create);
			builder.delete(table, idName, id);
			return item;
		});
	}
	
	default int delete(Database database, String table, String idName, Object id) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.delete(table, idName, id);
		});
	}

	default <S extends Entity> int update(Database database, String table, String idName, Object id, S entity) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.update(table, idName, id, entity.toMap());
		});
	}

	default <S extends Entity> DictionaryValue insert(Database database, String table, S entity) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.insert(table, entity.toMap());
		});
	}
	
}
