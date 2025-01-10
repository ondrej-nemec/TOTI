import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import ji.common.Log4j2LoggerTestImpl;
import ji.common.structures.DictionaryValue;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.database.support.DatabaseRow;

public class TimestampEndToEndTest {
	/*if (java.sql.Date.class.isInstance(object)) {
	return fromTime.apply(java.sql.Date.class.cast(object).toLocalDate());
}
if (Time.class.isInstance(object)) {
	return fromTime.apply(Time.class.cast(object).toLocalTime());
}
if (Timestamp.class.isInstance(object)) {
	Timestamp t = Timestamp.class.cast(object);
	return fromTime.apply(Timestamp.class.cast(object).toLocalDateTime());
}*/
	
	public static void main(String[] args) {
		//*
		System.out.println("FROM LONG");
		DictionaryValue t = new DictionaryValue(new Date().getTime());
		System.out.println(t.getValue());
		try {
			System.out.println("T  " + t.getTime());
		} catch (Exception e) { System.err.println("-> T " + e.getMessage()); }
		try {
			System.out.println("D  " + t.getDate());
		} catch (Exception e) { System.err.println("-> D " + e.getMessage()); }
		try {
			System.out.println("DT  " + t.getDateTime());
		} catch (Exception e) { System.err.println("-> DT " + e.getMessage()); }
		try {
			System.out.println("Z  " + t.getDateTimeZone());
		} catch (Exception e) { System.err.println("-> Z " + e.getMessage()); }
		//*/
		for (DatabaseConfig c : new DatabaseConfig[] {
				new DatabaseConfig(
						"mysql",
						"//localhost",
						"timestamps",
						"root",
						"", 
						Arrays.asList(),
						1
					),
				new DatabaseConfig(
						"postgresql",
						"//localhost",
						"timestamps",
						"postgres",
						"1234",
						Arrays.asList(),
						1
					)
		}) {
			Database db = new Database(c, new Log4j2LoggerTestImpl(null));
			System.out.println("-----------------");
			System.out.println("Database: " + c.type);
			try {
				db.applyBuilder((builder)->{
					DatabaseRow row = builder.select("*").from("times").fetchRow();
					
					try {
						System.out.println("  value: " + row.getValue("row_time") + " " + row.getValue("row_time").getClass());
						System.out.println("T  " + row.getTime("row_time"));
					} catch (Exception e) { System.err.println("-> T " + e.getMessage()); }
					try {
						System.out.println("  value: " + row.getValue("row_date") + " " + row.getValue("row_date").getClass());
						System.out.println("D  " + row.getDate("row_date"));
					} catch (Exception e) { System.err.println("-> D " + e.getMessage()); }
					try {
						System.out.println("  value: " + row.getValue("row_datetime") + " " + row.getValue("row_datetime").getClass());
						System.out.println("DT  " + row.getDateTime("row_datetime"));
					} catch (Exception e) { System.err.println("-> DT " + e.getMessage()); }
					try {
						System.out.println("  value: " + row.getValue("row_zoned") + " " + row.getValue("row_zoned").getClass());
						System.out.println("Z  " + row.getDateTimeZone("row_zoned"));
					} catch (Exception e) { System.err.println("-> Z " + e.getMessage()); }
					
					return null;
				});
				System.out.println(">>>>");
				db.applyQuery((con)->{
					ResultSet rs = con.createStatement().executeQuery("select * from times limit 1");
					rs.next();
					try {
						System.out.println("T  " + rs.getString("row_time"));
					} catch (Exception e) { System.err.println("-> T " + e.getMessage()); }
					try {
						System.out.println("D  " + rs.getString("row_date"));
					} catch (Exception e) { System.err.println("-> D " + e.getMessage()); }
					try {
						System.out.println("DT  " + rs.getString("row_datetime"));
					} catch (Exception e) { System.err.println("-> DT " + e.getMessage()); }
					try {
						System.out.println("Z  " + rs.getString("row_zoned"));
					} catch (Exception e) { System.err.println("-> Z " + e.getMessage()); }
					return null;
				});
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		System.out.println();
		System.out.println("********");
		/*DictionaryValue value = new DictionaryValue(new Date());
		System.out.println("  DATE");
		System.out.println("  value: " + value.getValue() + " " + value.getValue().getClass());
		try {
			System.out.println("T  " + value.getTime());
		} catch (Exception e) { System.err.println("-> T " + e.getMessage()); }
		try {
			System.out.println("D  " + value.getDate());
		} catch (Exception e) { System.err.println("-> D " + e.getMessage()); }
		try {
			System.out.println("DT  " + value.getDateTime());
		} catch (Exception e) { System.err.println("-> DT " + e.getMessage()); }
		try {
			System.out.println("Z  " + value.getDateTimeZone());
		} catch (Exception e) { System.err.println("-> Z " + e.getMessage()); }
		*/
	}
	/*
	public static void main(String[] args) {
		
		System.out.println("FROM LONG");
		DictionaryValue t = new DictionaryValue(new Date().getTime());
		System.out.println(t.getValue());
		try {
			System.out.println("T  " + t.getTime());
		} catch (Exception e) { System.err.println("T -> " + e.getMessage()); }
		try {
			System.out.println("D  " + t.getDate());
		} catch (Exception e) { System.err.println("D -> " + e.getMessage()); }
		try {
			System.out.println("DT  " + t.getDateTime());
		} catch (Exception e) { System.err.println("DT -> " + e.getMessage()); }
		try {
			System.out.println("Z  " + t.getDateTimeZone());
		} catch (Exception e) { System.err.println("Z -> " + e.getMessage()); }
		
		for (DatabaseConfig c : new DatabaseConfig[] {
				new DatabaseConfig(
						"mysql", 
						"//localhost", 
						true,
						"timestamps", 
						"root",
						"", 
						Arrays.asList(),
						1
					),
				new DatabaseConfig(
						"postgresql", 
						"//localhost", 
						true,
						"timestamps", 
						"postgres",
						"1234",
						Arrays.asList(),
						1
					)
		}) {
			Database db = new Database(c, getLogger());
			System.out.println("-----------------");
			System.out.println("Database: " + c.type);
			try {
				db.applyBuilder((builder)->{
					DatabaseRow row = builder.select("*").from("times").fetchRow();
					for (String s : new String[] {"row_time", "row_date", "row_datetime", "row_zoned"}) {
						System.out.println("  COL: " + s);
						System.out.println("  value: " + row.getValue(s) + " " + row.getValue(s).getClass());
						try {
							System.out.println("T  " + row.getTime(s));
						} catch (Exception e) { System.err.println("T -> " + e.getMessage()); }
						try {
							System.out.println("D  " + row.getDate(s));
						} catch (Exception e) { System.err.println("D -> " + e.getMessage()); }
						try {
							System.out.println("DT  " + row.getDateTime(s));
						} catch (Exception e) { System.err.println("DT -> " + e.getMessage()); }
						try {
							System.out.println("Z  " + row.getDateTimeZone(s));
						} catch (Exception e) { System.err.println("Z -> " + e.getMessage()); }
						System.out.println();
					}
					return null;
				});
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				// e.printStackTrace();
			}
		}
		
		System.out.println();
		System.out.println("********");
		DictionaryValue value = new DictionaryValue(new Date());
		System.out.println("  DATE");
		System.out.println("  value: " + value.getValue() + " " + value.getValue().getClass());
		try {
			System.out.println("T  " + value.getTime());
		} catch (Exception e) { System.err.println("T -> " + e.getMessage()); }
		try {
			System.out.println("D  " + value.getDate());
		} catch (Exception e) { System.err.println("D -> " + e.getMessage()); }
		try {
			System.out.println("DT  " + value.getDateTime());
		} catch (Exception e) { System.err.println("DT -> " + e.getMessage()); }
		try {
			System.out.println("Z  " + value.getDateTimeZone());
		} catch (Exception e) { System.err.println("Z -> " + e.getMessage()); }
		
	}
	*/
	
	
}
