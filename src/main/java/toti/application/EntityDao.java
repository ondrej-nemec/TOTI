package toti.application;

import java.sql.SQLException;
import java.util.List;

import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.builders.UpdateBuilder;

public interface EntityDao<T extends Entity> {
	
	Database getDatabase();
	
	String getTableName();
	
	T createEntity(DatabaseRow row);

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
	
}
