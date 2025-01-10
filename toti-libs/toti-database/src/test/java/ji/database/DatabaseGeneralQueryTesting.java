package ji.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.Log4j2LoggerTestImpl;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.enums.ColumnType;

public class DatabaseGeneralQueryTesting {

	public static void main(String[] args) throws SQLException {
		List<String> migrationPath = Arrays.asList("test/database");
		String dbName = "toti2";
		List<DatabaseConfig> configs = Arrays.asList(
			//*
			new DatabaseConfig(
				"mysql", "//localhost", dbName,
				"root", "", migrationPath, 5
			),
			//*/
			//*
			new DatabaseConfig(
				"derby", "C:\\software\\DerbyDB\\bin", dbName,
				"root", "", migrationPath, 5
			),
			//*/
			//*	
			new DatabaseConfig(
				"postgresql", "//localhost", dbName,
				"postgres", "1234", migrationPath, 5
			)
			//*/
		);
				
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 10);
	//	filters.put("name", null); // "Name #0"
		filters.put("description", "Desc #0");
	//	filters.put("is_active", true);
		Map<String, Object> sorting = new HashMap<>();
		sorting.put("name", "ASC");
		sorting.put("description", "Desc");
		sorting.put("id", "DESC");
		
		for (DatabaseConfig config : configs) {
			System.out.println("Database " + config.type);
			try {
				Database database = new Database(config, new Log4j2LoggerTestImpl(null));
				database.createDbAndMigrate();
				System.out.println("Migrated");
				//*
				List<Map<String, Object>> data = database.applyBuilder((builder)->{
					List<Map<String, Object>> items = new LinkedList<>();
					SelectBuilder select = builder.select("*").from("query_test");
					select.where("1=1");
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
						select.where(where)
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
					System.out.println(select.createSql());
					select.fetchAll().forEach((row)->{
						items.add(row.getValues());
					});
					return items;
				});
				System.out.println("Data:");
				data.forEach((row)->System.out.println("  " + row));
				//*/
				
				System.out.println("Database finish");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("end");
	}
}
