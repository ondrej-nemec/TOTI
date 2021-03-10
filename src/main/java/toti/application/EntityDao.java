package toti.application;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import database.Database;
import database.support.DatabaseRow;
import querybuilder.ColumnType;
import querybuilder.InsertQueryBuilder;
import querybuilder.SelectQueryBuilder;
import querybuilder.UpdateQueryBuilder;

public interface EntityDao {
	
	GridDataSet getAll(
			int pageIndex,
			int pageSize, 
			Map<String, Object> filters,
			Map<String, Object> sorting,
			Collection<Object> forOwners) throws SQLException;
	
	Map<String, Object> get(int id) throws SQLException;
	
	Map<String, Object> delete(int id) throws SQLException;
	
	void update(int id, Map<String, Object> values) throws SQLException;
	
	int insert(Map<String, Object> values) throws SQLException;
	
	int getTotalCount() throws SQLException;
	
	Map<String, Object> getHelp(Collection<Object> forOwners) throws SQLException;
	
	default GridDataSet getAll(
			Database database,
			String table,
			Optional<String> ownerColumnName,
			int pageIndex,
			int pageSize, 
			Map<String, Object> filters,
			Map<String, Object> sorting,
			Collection<Object> forOwners) throws SQLException {
		return database.applyBuilder((builder)->{
			SelectQueryBuilder select = builder.select("*").from(table);
			select.where("1=1");
			if (ownerColumnName.isPresent()) {
				if (!forOwners.isEmpty()) {
					select.andWhere(ownerColumnName.get() + " in (:in)")
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
			//select.limit(pageSize, (pageIndex-1)*pageSize);
			List<DatabaseRow> rows = select.fetchAll();
			List<Object> items = new LinkedList<>();
			rows.subList((pageIndex-1)*pageSize, Math.min(pageIndex*pageSize, rows.size())).forEach((row)->{
				items.add(row.getValues());
			});
			return new GridDataSet(items, rows.size(), pageIndex);
		});
	}
	
	default Map<String, Object> get(Database database, String table, int id) throws SQLException {
		return database.applyBuilder((builder)->{
			return builder.select("*").from(table).where("id = :id").addParameter(":id", id).fetchRow().getValues();
		});
	}
	
	default Map<String, Object> delete(Database database, String table, int id) throws SQLException {
		return database.applyBuilder((builder)->{
			Map<String, Object> item = builder.select("*").from(table).where("id = :id").addParameter(":id", id).fetchRow().getValues();
			builder.delete(table).where("id = :id").addParameter(":id", id).execute();
			return item;
		});
	}
	
	default void update(Database database, String table, int id, Map<String, Object> values) throws SQLException {
		database.applyBuilder((builder)->{
			UpdateQueryBuilder b = builder.update(table);
			values.forEach((name, value)->{
				b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
			});
			b.where("id = :id").addParameter(":id", id);
			b.execute();
			return null;
		});
	}
	
	default int insert(Database database, String table, Map<String, Object> values) throws SQLException {
		return database.applyBuilder((builder)->{
			InsertQueryBuilder b = builder.insert(table);
			values.forEach((name, value)->{
				b.addValue(name, value);
			});
			return Integer.parseInt(b.execute().toString());
		});
	}
	
	default int getTotalCount(Database database, String table) throws SQLException {
		return database.applyBuilder((builder)->{
			return Integer.parseInt(builder.select("count(*)").from(table).fetchSingle().toString());
		});
	}
	
	default Map<String, Object> getHelp(
			Database database,
			String table,
			Optional<String> ownerColumnName,
			Collection<Object> forOwners,
			String keyName,
			String valueName) throws SQLException {
		return database.applyBuilder((builder)->{
			Map<String, Object> items = new HashMap<>();
			SelectQueryBuilder select = builder.select(keyName + "," + valueName).from(table);
			if (ownerColumnName.isPresent()) {
				if (!forOwners.isEmpty()) {
					select.where(ownerColumnName.get() + " in (:in)").addParameter(":in", forOwners);
				} else {
					select.where("1=2"); // no results
				}
			}
			select.fetchAll().forEach((row)->{
				items.put(row.getString(keyName), row.getValue(valueName));
			});
			return items;
		});
	}
	
}
