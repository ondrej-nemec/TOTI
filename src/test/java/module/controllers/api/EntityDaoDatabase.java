package module.controllers.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import database.Database;
import querybuilder.ColumnType;
import querybuilder.InsertQueryBuilder;
import querybuilder.SelectQueryBuilder;
import querybuilder.UpdateQueryBuilder;

public class EntityDaoDatabase implements EntityDao {
	
	private final Database database;
	private final String table = "entity";
	
	public EntityDaoDatabase(Database database) {
		this.database = database;
	}

	@Override
	public List<Map<String, Object>> getAll(int pageIndex, int pageSize, Map<String, Object> filters,
			Map<String, Object> sorting) throws Exception {
		return database.applyBuilder((builder)->{
			List<Map<String, Object>> items = new LinkedList<>();
			SelectQueryBuilder select = builder.select("*").from(table);
			select.where("1=1");
			/*filters.forEach((filter, value)->{
				select.andWhere("concat('', " + filter + ") like :" + filter + "Value")
					.addParameter(":" + filter + "Value", value + "%");
			});*/
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
			select.limit(pageSize, (pageIndex-1)*pageSize);
			select.fetchAll().forEach((row)->{
				items.add(row.getValues());
			});
			return items;
		});
	}

	@Override
	public Map<String, Object> get(int id) throws Exception {
		return database.applyBuilder((builder)->{
			return builder.select("*").from(table).where("id = :id").addParameter(":id", id).fetchRow().getValues();
		});
	}

	@Override
	public Map<String, Object> delete(int id) throws Exception {
		return database.applyBuilder((builder)->{
			Map<String, Object> item = builder.select("*").from(table).where("id = :id").addParameter(":id", id).fetchRow().getValues();
			builder.delete(table).where("id = :id").addParameter(":id", id).execute();
			return item;
		});
	}

	@Override
	public void update(int id, Map<String, Object> values) throws Exception {
		database.applyBuilder((builder)->{
			UpdateQueryBuilder b = builder.update(table);
			values.forEach((name, value)->{
				b.set(String.format("%s = :%s", name, name)).addParameter(":" + name, value);
			});
			b.execute();
			return null;
		});
	}

	@Override
	public int insert(Map<String, Object> values) throws Exception {
		return database.applyBuilder((builder)->{
			InsertQueryBuilder b = builder.insert(table);
			values.forEach((name, value)->{
				b.addValue(name, value);
			});
			return Integer.parseInt(b.execute().toString());
		});
	}

	@Override
	public int getTotalCount() throws Exception {
		return database.applyBuilder((builder)->{
			return Integer.parseInt(builder.select("count(*)").from(table).fetchSingle().toString());
		});
	}

}
