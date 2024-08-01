package ji.querybuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Terminal;
import ji.common.structures.ThrowingBiConsumer;
import ji.database.support.DatabaseRow;
import ji.files.text.Text;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.Join;
import ji.querybuilder.enums.OnAction;
import ji.querybuilder.enums.Where;
import ji.querybuilder.instances.DerbyQueryBuilder;
import ji.querybuilder.instances.MySqlQueryBuilder;
import ji.querybuilder.instances.PostgreSqlQueryBuilder;
import ji.querybuilder.instances.SqlServerQueryBuilder;

@RunWith(Parameterized.class)
public class QueryBuilderEndToEndTest {
	
	private final static String DB_NAME = "javainit_database_querybuilder";
	
	private final Function<Connection, QueryBuilder> createBuilder;
	
	private final Supplier<Connection> createConnection;
	
	private final Consumer<Void> startDbAndCreateSchema;
	
	private final Consumer<Void> stopDb;
	
	private final String type;
	
	public QueryBuilderEndToEndTest(
			DbInstance instance,
			Supplier<Connection> createConnection,
			Consumer<Void> startDbAndCreateSchema,
			Consumer<Void> stopDb,
			String type) {
		this.createBuilder = (c)->new QueryBuilder(instance, c);
		this.createConnection = createConnection;
		this.startDbAndCreateSchema = startDbAndCreateSchema;
		this.stopDb = stopDb;
		this.type = type;
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		Terminal terminal = new Terminal(Mockito.mock(Logger.class));
		
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", "");
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		
		List<Object[]> result = new LinkedList<>();
		//*
		result.add(createParams(
				new MySqlQueryBuilder(),
				() -> {
					try {
						return DriverManager.getConnection("jdbc:mysql://localhost/" + DB_NAME, props);
					} catch (SQLException e) {
						fail("Connection not created: " + e.getMessage());
						e.printStackTrace();
						return null;
					}
				},
				(ignored) -> {
					try {
						DriverManager
						.getConnection("jdbc:mysql://localhost/", props)
						.createStatement()
						.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
					} catch (SQLException e) {
						fail("Connection not created: " + e.getMessage());
						e.printStackTrace();
					}
				},
				(ignored) -> {
					// not required
				}, 
				"mysql"
		));
		//*/
		//*
	  	String derbyPath = "C:\\software\\DerbyDB\\bin";
		result.add(createParams(
				new DerbyQueryBuilder(),
				() -> {
					try {
						return DriverManager.getConnection("jdbc:derby:" +derbyPath + "/" + DB_NAME, props);
					} catch (SQLException e) {
						fail("Connection not created: " + e.getMessage());
						return null;
					}
				},
				(ignored) -> {
					terminal.runFile(derbyPath + "/startNetworkServer");
					try { DriverManager.getConnection("jdbc:derby:" +derbyPath + "/" + DB_NAME, props).close();
					} catch (SQLException e) {
						fail("Db not created: " + e.getMessage());
						e.printStackTrace();
					}
				},
				(ignored) -> {
					terminal.runFile(derbyPath + "/stopNetworkServer");
				}, 
				"derby"
		));
		//*/
		//*
		result.add(createParams(
				new PostgreSqlQueryBuilder(),
				() -> {
					try {
						props.setProperty("user", "postgres");
						props.setProperty("password", "1234");
						return DriverManager.getConnection("jdbc:postgresql://localhost/" + DB_NAME, props);
					} catch (SQLException e) {
						fail("Connection not created: " + e.getMessage());
						return null;
					}
				},
				(ignored) -> {
					props.setProperty("user", "postgres");
					props.setProperty("password", "1234");
					try (Connection con = DriverManager
							.getConnection("jdbc:postgresql://localhost/", props)) {
						PreparedStatement stmt = con.prepareStatement("SELECT FROM pg_database WHERE datname = ?");
						stmt.setString(1, DB_NAME);
						ResultSet rs = stmt.executeQuery();
						if (!rs.next()) {
							con.createStatement().executeUpdate(String.format("CREATE DATABASE %s", DB_NAME));
						}
					} catch (Exception e) {
						fail("Connection not created: " + e.getMessage());
					}
				},
				(ignored) -> {}, 
				"postgresql"
		));
		//*/
		//*
		result.add(createParams(
                new SqlServerQueryBuilder(),
                () -> {
                    try {
                        return DriverManager.getConnection("jdbc:sqlserver://localhost;Database=" + DB_NAME + ";integratedSecurity=true", props);
                    } catch (SQLException e) {
                        fail("Connection not created: " + e.getMessage());
                        return null;
                    }
                },
                (ignored) -> {
                        try {
                            DriverManager
                            .getConnection("jdbc:sqlserver://localhost" + ";integratedSecurity=true", props)
                            .createStatement()
                            .executeUpdate(String.format(
                                "IF NOT EXISTS("
                                + "SELECT * FROM sys.databases WHERE name = '%s'"
                                + ")"
                                + " BEGIN" + 
                                "    CREATE DATABASE %s" + 
                                "  END",
                                DB_NAME, DB_NAME
                            ));
                        } catch (Exception e) {
                            fail("Connection not created: " + e.getMessage());
                        }
                    },
                (ignored) -> {}, 
                "sqlserver"
        ));
		//*/
		return result;
	}
	
	
	private static Object[] createParams(
			DbInstance instance,
			Supplier<Connection> createConnection,
			Consumer<Void> startDbAndCreateSchema,
			Consumer<Void> stopDb,
			String type) {
		return new Object[] {instance, createConnection, startDbAndCreateSchema, stopDb, type};
	}
	
	/***************************************/

	@Before
	public void before() throws SQLException, IOException {
		startDbAndCreateSchema.accept(null);
	}
	
	@After
	public void after() throws SQLException {
		stopDb.accept(null);
	}
	
	private void test(String file, ThrowingBiConsumer<Connection, QueryBuilder, Exception> test, List<String> tables) throws Exception {
		test(file, test, tables, new LinkedList<>());
	}
	
	private void test(String file, ThrowingBiConsumer<Connection, QueryBuilder, Exception> test, List<String> tables, List<String> views) throws Exception {
		Connection connection = createConnection.get();
		
		Exception ex = null;
		try {
			if (file != null) {
				//try {
				loadDb(connection, file);
				/*} catch (Exception e) {
					System.err.println(file);
					System.err.println(tables);
					e.printStackTrace();
				}*/
			}
			test.accept(connection, createBuilder.apply(connection));
		} catch (Exception e) {
			ex = e;
		} finally {
			dropViews(connection, views);
			dropTables(connection, tables);
			
			connection.close();
		}
		if (ex != null) {
			System.err.println("QueryBuilder Error, file: " + file);
			ex.printStackTrace();
			throw ex;
		}
	}
	
	private void dropTables(Connection connection, List<String> tables) throws SQLException {
		for (String table : tables) {
			try {
				connection.createStatement().executeUpdate(String.format("drop table %s", table));
			} catch (Exception e) { /* ignored*/ }
		}
	}
	
	private void dropViews(Connection connection, List<String> views) throws SQLException {
		for (String view : views) {
			try {
				connection.createStatement().executeUpdate(String.format("drop view %s", view));
			} catch (Exception e) { /* ignored*/ }
		}
	}
	
	private void loadDb(Connection connection, String file) throws IOException, SQLException {
		String sql = Text.get().read((br)->{
			return br.asString();
		}, getClass().getResourceAsStream("/querybuilder/" + type + "/" + file + ".sql"));
		Statement stat = connection.createStatement();
		String[] batches = sql.split(";");
		for (String batch : batches) {
            stat.execute(batch.trim());
        }
		/*for (String batch : batches) {
		    stat.addBatch(batch.trim());
		}
		stat.executeBatch();
		stat.close();*/
	}

	/*************************************/

	@Test
	public void testExecuteUpdate() throws Exception {
		test("update", (conn, builder)-> {
			builder.update("update_table")
    			.set("name=%set")
    			.where("id > %id")
    			.where("name=%whereName")
    			.where("name=%orName", Where.OR)
    			.addParameter("%set", "setted name")
    			.addParameter("%id", 1)
    			.addParameter("%whereName", "set it")
    			.addParameter("%orName", "this too")
    			.execute();
		
    		ResultSet res = conn.createStatement().executeQuery("SELECT * FROM update_table order by id");
    		
    		int id = 1;
    		while(res.next()) {
    			switch(id) {
    				case 1: assertRow(res, id, "set it");break;
    				case 2: assertRow(res, id, "setted name");break;
    				case 3: assertRow(res, id, "setted name");break;
    				case 4: assertRow(res, id, "this not");break;
    				default: throw new RuntimeException("Upgrade your tests!");
    			}
    			id++;
    		}
		}, Arrays.asList("update_table"));
	}
	
	private void assertRow(ResultSet res, int id, String name) throws SQLException {
		assertEquals(id, res.getInt("id"));
		assertEquals(name, res.getString("name"));
	}

	@Test
	public void testExecuteDelete() throws Exception {
		test("delete", (conn, builder)->{
			int code = builder
				.delete("delete_table")
				.where("id > %id")
				.where("name=%whereName")
				.where("name=%orWhere", Where.OR)
				.addParameter("%id", 1)
				.addParameter("%whereName", "delete this")
				.addParameter("%orWhere", "this too")
				.execute();
			
			assertEquals(2, code);
			
			ResultSet res = conn.createStatement().executeQuery("SELECT * FROM delete_table");
			
			int id = 1;
			while(res.next()) {
				switch(id) {
					case 1: assertRow(res, id, "delete this");break;
					case 2: assertRow(res, 4, "this not");break;
					default: throw new RuntimeException("Upgrade your tests!");
				}
				id++;
			}
		}, Arrays.asList("delete_table"));
	}
	
	@Test
	public void testExecuteInsert() throws Exception {
		test("insert", (conn, builder)->{
			Object code = builder
					.insert("insert_table")
					.addValue("id", 1)
					.addValue("name", "column_name")
					.execute();
				assertEquals(1, Integer.parseInt(code.toString()));
				
				ResultSet res = conn.createStatement().executeQuery("SELECT * FROM insert_table");
				
				int id = 1;
				while(res.next()) {
					switch(id) {
						case 1: assertRow(res, id, "column_name");break;
						default: throw new RuntimeException("Upgrade your tests!");
					}
					id++;
				}
		}, Arrays.asList("insert_table"));
	}
	
	@Test
	@Ignore
	public void testExecuteSelectWithoutGroupBy() throws Exception {
		test("select", (conn, builder) -> {
			SelectBuilder res = builder
					.select("a.id a_id, b.id b_id, a.name a_name, b.name b_name")
					.from("select_table a")
					.join("joined_table b", Join.INNER_JOIN, "a.id = b.a_id")
					.where("a.id > %id")
					.where("a.name = %a_name")
					.where("b.name = %b_name", Where.OR)
					.orderBy("a.id ASC")
					.limit(2, 0)
					.addParameter("%id", 1)
					.addParameter("%a_name", "name_a")
					.addParameter("%b_name", "name_b");
				
				String expectedSingle = "2";
				DatabaseRow expectedRow = new DatabaseRow();
				expectedRow.addValue("a_id", "2");
				expectedRow.addValue("b_id", "3");
				expectedRow.addValue("a_name", "name 2");
				expectedRow.addValue("b_name", "name_b");
				

				DatabaseRow expectedRow2 = new DatabaseRow();
				expectedRow2.addValue("a_id", "4");
				expectedRow2.addValue("b_id", "7");
				expectedRow2.addValue("a_name", "name_a");
				expectedRow2.addValue("b_name", "name 7");
				
				assertEquals(expectedSingle, res.fetchSingle());
				assertEquals(expectedRow, res.fetchRow());
				assertEquals(Arrays.asList(expectedRow, expectedRow2), res.fetchAll());	
		}, Arrays.asList("select_table", "joined_table"));
	}
		
	@Test
	public void testExecuteSelect() throws Exception {
		test("select", (conn, builder) -> {
			SelectBuilder res = builder
					.select("a.id a_id, b.id b_id, a.name a_name, b.name b_name")
					.from("select_table a")
					.join("joined_table b", Join.INNER_JOIN, "a.id = b.a_id")
					.where("a.id > %id")
					.where("a.name = %a_name")
					.where("b.name = %b_name", Where.OR)
					.groupBy("a.id, b.id, a.name, b.name")
					.having("a.id < %a_id")
					.orderBy("a.id ASC, b.id ASC")
					.limit(2, 0)
					.addParameter("%id", 1)
					.addParameter("%a_name", "name_a")
					.addParameter("%b_name", "name_b")
					.addParameter("%a_id", 6);
				
				Object expectedSingle = 2;
				DatabaseRow expectedRow = new DatabaseRow();
				expectedRow.addValue("a_id", 2);
				expectedRow.addValue("b_id", 3);
				expectedRow.addValue("a_name", "name 2");
				expectedRow.addValue("b_name", "name_b");
				

				DatabaseRow expectedRow2 = new DatabaseRow();
				expectedRow2.addValue("a_id", 4);
				expectedRow2.addValue("b_id", 5);
				expectedRow2.addValue("a_name", "name_a");
				expectedRow2.addValue("b_name", "name 5");
				
				assertEquals(expectedSingle, res.fetchSingle().getValue());
				assertEquals(expectedRow, res.fetchRow());
				assertEquals(Arrays.asList(expectedRow, expectedRow2), res.fetchAll());	
		}, Arrays.asList("select_table", "joined_table"));
	}
	
	@Test
	public void testExecuteDeleteTable() throws Exception {
		test("tableDelete", (conn, builder)->{
			builder
			.deleteTable("table_to_delete")
			.execute();
		
    		try {
    			conn.createStatement().executeQuery("SELECT * FROM table_to_delete");
    			fail("Table exist");
    		} catch (SQLException e) {
    			assertTrue(true);
    		}
		}, Arrays.asList("table_to_delete"));
	}
	
	@Test
	public void testExecuteDeleteView() throws Exception {
		test("viewDelete", (conn, builder)->{			
			builder
			.deleteView("view_to_delete")
			.execute();
		
    		try {
    			conn.createStatement().executeQuery("SELECT * FROM view_to_delete");
    			fail("Table exist");
    		} catch (SQLException e) {
    			assertTrue(true);
    		}
		}, Arrays.asList("view_to_delete"));
	}
	
	@Test
	public void testExecuteIndexeses() throws Exception {
		test("indexes", (conn, builder)->{
			builder.createIndex("index_name", "table1", "id").execute();
			builder.deleteIndex("index_name").execute();
		}, Arrays.asList("table1"));
	}
	
	@Test
	public void testCreateTable() throws Exception {
		test("createTable", (conn, builder)->{
		    builder
				.createTable("create_table")
				.addColumn("c1", ColumnType.bool(), true, ColumnSetting.NOT_NULL)
				.addColumn("c2", ColumnType.integer())
				.addColumn("c3", ColumnType.string(10), ColumnSetting.UNIQUE)
				.addForeignKey("c2", "SecondTable", "second_id", OnAction.NO_ACTION, OnAction.NO_ACTION)
				.execute();
			
			conn.createStatement().executeQuery("select c1, c2, c3 from create_table");
		}, Arrays.asList("create_table", "SecondTable"));
	}
	
	@Test
	public void testAlterTable() throws Exception {
		test("alterTable", (conn, builder)->{
    		// builder.alterTable("alter_table").modifyColumnType("id", ColumnType.bool()).execute();
    		builder.alterTable("alter_table").modifyColumnType("id", ColumnType.string(11)).execute();
    		
    		builder.alterTable("alter_table").renameColumn("name", "name2", ColumnType.string(10)).execute();
    		conn.createStatement().executeQuery("select name2 from alter_table");
    					
    		builder.alterTable("alter_table").addColumn("number", ColumnType.integer()).execute();
    		conn.createStatement().executeQuery("select number from alter_table");
    		
    		builder.alterTable("alter_table").deleteColumn("number").execute();
    		try {
    			conn.createStatement().executeQuery("select number from alter_table");
    			fail("Column number exists");
    		} catch (SQLException ignored) {}
        		
    		builder.alterTable("alter_table").addForeignKey("fKey", "table_fk", "id").execute();
    		
    		builder.alterTable("alter_table").deleteForeingKey("fKey").execute();
		}, Arrays.asList("alter_table", "table_fk"));
	}
	
	@Test
	public void testCreateView() throws Exception {
		test(
			"createView",
			(conn, builder)->{
    			builder
    				.createView("test_view")
    				.select("a.id a_id, b.id b_id, a.name")
    				.from("table1 a")
    				.join("table2 b", Join.INNER_JOIN, "a.id = b.id")
    				.where("a.id != :id1")
    				.addParameter(":id1", 0)
    				.groupBy("a.id, b.id, a.name")
    				.having("b.id != :id2")
    				.orderBy("a.id")
    				.addParameter(":id2", 0)
    				.limit(5, 0)
    				.execute();
    			
    			ResultSet rs = conn.createStatement().executeQuery("select * from test_view order by a_id");
    			int i = 0;
    			while (rs.next()) {
    				i++;
    				if (i > 5) {
    					fail("More results");
    				}
    				assertEquals(i, rs.getInt("a_id"));
    				assertEquals("name_" + i, rs.getString("name"));
    				assertEquals(i, rs.getInt("b_id"));
    			}
    		}, Arrays.asList("table1", "table2"), Arrays.asList("test_view")
		);
	}
	
	@Test
	public void testAlterView() throws Exception {
		test("alterView", (conn, builder)->{
		    builder.alterView("test_view")
    			.select("a.id a_id, b.id b_id, a.name")
    			.from("table1 a")
    			.join("table2 b", Join.INNER_JOIN, "a.id = b.id")
    			.where("a.id != :id1")
    			.addParameter(":id1", 0)
    			.groupBy("a.id, b.id, a.name")
    			.having("b.id != :id2")
    			.addParameter(":id2", 0)
    			.orderBy("a.id")
    			.limit(4, 0)
    			.execute();
    		
    		ResultSet rs = conn.createStatement().executeQuery("select * from test_view order by a_id");
    		int i = 0;
    		while (rs.next()) {
    			i++;
    			if (i > 4) {
    				fail("More results");
    			}
    			assertEquals(i, rs.getInt("a_id"));
    			assertEquals("name_" + i, rs.getString("name"));
    			assertEquals(i, rs.getInt("b_id"));
    		}
		}, Arrays.asList("table1", "table2"), Arrays.asList("test_view"));
	}
	
}
