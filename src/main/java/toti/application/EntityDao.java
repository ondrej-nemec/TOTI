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
/*
	default SelectBuilder _getAll(String select, QueryBuilder builder) {
		return builder.select(select).from(getTableName());
	}
*/
	default List<T> getAll() throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			return builder.getAll(builder, getTableName(), row->createEntity(row));
			/*return _getAll("*", builder)
			.fetchAll((row)->{
				return createEntity(row);
			});*/
		});
	}
/*
	default SelectBuilder _get(QueryBuilder builder, Object id) {
		return builder.select("*").from(getTableName())
				.where(getIdName() + " = :id").addParameter(":id", id);
	}
*/
	default T get(Object id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			// return createEntity(_get(builder, id).fetchRow());
			return createEntity(builder.get(builder, getTableName()));
		});
	}
	
	default T delete(Object id) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			T item = get(id);
			//builder.delete(getTableName()).where(getIdName() + " = :id").addParameter(":id", id).execute();
			builder.delete(builder, getTableName(), getIdName(), id);
			return item;
		});
	}
/*
	default int _update(QueryBuilder builder, Object id, T entity) throws SQLException {
		UpdateBuilder b = builder.update(getTableName());
		entity.toMap().forEach((name, value)->{
			b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
		});
		b.where(getIdName() + " = :id").addParameter(":id", id);
		return b.execute();
	}
*/
	default int update(Object id, T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			// return _update(builder, id, entity);
			return builder.update(builder, getTableName(), getIdName(), entity.toMap(), id);
		});
	}
/*
	default DictionaryValue _insert(QueryBuilder builder, T entity) throws SQLException {
		InsertBuilder b = builder.insert(getTableName());
		entity.toMap().forEach((name, value)->{
			b.addValue(name, value);
		});
		return b.execute();
	}
*/
	default DictionaryValue insert(T entity) throws SQLException {
		return getDatabase().applyBuilder((builder)->{
			// return _insert(builder, entity);
			return builder.insert(builder, getTableName(), entity.toMap());
		});
	}
	
}
