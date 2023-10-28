package toti.ui.backend;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.builders.SelectBuilder;

public interface EntityDao<T extends Entity> {
	
	Database getDatabase();
	
	String getTableName();
	
	T createEntity(DatabaseRow row);

	default String getIdName() {
		return "id";
	}

	default List<T> getAll() throws SQLException {
		return getAll(getDatabase(), getTableName(), row->createEntity(row));
	}

	default T get(Object id) throws SQLException {
		return get(getDatabase(), getTableName(), getIdName(), id, row->createEntity(row));
	}

	default boolean exists(Object id) throws SQLException {
		return exists(getDatabase(), getTableName(), getIdName(), id);
	}
	
	default boolean exists(Object id, Map<String, Object> params) throws SQLException {
        return exists(getDatabase(), getTableName(), getIdName(), id, params);
    }
	
	default T delete(Object id) throws SQLException {
		return delete(getDatabase(), getTableName(), getIdName(), id, row->createEntity(row));
	}
	
	default T delete(Object id, TransactionListener<T> listener) throws SQLException {
		return delete(getDatabase(), getTableName(), getIdName(), id, row->createEntity(row), listener);
	}

	default int update(Object id, T entity) throws SQLException {
		return update(getDatabase(), getTableName(), getIdName(), id, entity);
	}

	default int update(Object id, T entity, TransactionListener<T> listener) throws SQLException {
		return update(getDatabase(), getTableName(), getIdName(), id, entity, listener);
	}

	default DictionaryValue insert(T entity) throws SQLException {
		return insert(getDatabase(), getTableName(), entity);
	}

	default DictionaryValue insert(T entity, TransactionListener<T> listener) throws SQLException {
		return insert(getDatabase(), getTableName(), entity, listener);
	}
	
	/********************************/

	default <S extends Entity> List<S> getAll(Database database, String table, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.getAll(table, create);
		});
	}

	default <S extends Entity> S get(Database database, String table, String idName, Object id, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return database.applyBuilder((builder)->{
			DatabaseRow data = builder.get(table, idName, id);
			if (data == null) {
				 return null;
			}
			return create.apply(data);
		});
	}

	default boolean exists(Database database, String table, String idName, Object id) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.get(table, idName, id, idName) != null; // select only name of id
		});
	}
	
	default boolean exists(Database database, String table, String idName, Object id, Map<String, Object> params) throws SQLException {
        return database.applyBuilder((builder)->{
             SelectBuilder select = builder.select(idName)
                     .from(table);
             if (id != null) {
                 select.where(idName + " != :" + idName)
                      .addParameter(":" + idName, id);
             } else {
                 select.where("1=1");
             }   
             params.forEach((name, value)->{
                 select.andWhere(name + " = :" + name)
                      .addParameter(":" + name, value);
             });
             return select.fetchRow() != null;
         });
    }
	
	default <S extends Entity> S delete(Database database, String table, String idName, Object id, ThrowingFunction<DatabaseRow, S, SQLException> create) throws SQLException {
		return delete(database, table, idName, id, create, new TransactionListener<S>() {});
	}
	
	default <S extends Entity> S delete(
			Database database, String table, String idName, Object id, 
			ThrowingFunction<DatabaseRow, S, SQLException> create, TransactionListener<S> listener) throws SQLException {
		return database.applyBuilder((builder)->{
            DatabaseRow data = builder.get(table, idName, id);
            S item = create.apply(data);
            listener.onTransactionStart(id, item);
            builder.delete(table, idName, id);
            listener.onTransactionEnd(id, item);
            return item;
		});
	}
	
	default int delete(Database database, String table, String idName, Object id) throws SQLException {
		return delete(database, table, idName, id,  new TransactionListener<Void>(){});
	}
	
	default int delete(Database database, String table, String idName, Object id, TransactionListener<Void> listener) throws SQLException {
		return database.applyBuilder((builder)->{
			listener.onTransactionStart(id, null);
			int res = builder.delete(table, idName, id);
			listener.onTransactionEnd(id, null);
			return res;
		});
	}

	default <S extends Entity> int update(Database database, String table, String idName, Object id, S entity) throws SQLException {
		return update(database, table, idName, id, entity,  new TransactionListener<S>(){});
	}

	default <S extends Entity> int update(Database database, String table, String idName, Object id, S entity, TransactionListener<S> listener) throws SQLException {
		return database.applyBuilder((builder)->{
			listener.onTransactionStart(id, entity);
			int updated = builder.update(table, idName, id, entity.toMap());
			listener.onTransactionEnd(id, entity);
			return updated;
		});
	}

	default <S extends Entity> DictionaryValue insert(Database database, String table, S entity) throws SQLException {
		return insert(database, table, entity, new TransactionListener<S>(){});
	}

	default <S extends Entity> DictionaryValue insert(Database database, String table, S entity, TransactionListener<S> listener) throws SQLException {
		return database.applyBuilder((builder)->{
			listener.onTransactionStart(null, entity);
			DictionaryValue id = builder.insert(table, entity.toMap());
			listener.onTransactionEnd(id.getValue(), entity);
			return id;
		});
	}
	
}
