package ji.testing;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import ji.database.Database;
import ji.testing.entities.Row;
import ji.testing.entities.Table;
import toti.files.env.Env;

public class DatabaseTestCaseTest extends DatabaseTestCase {

	private final Database realDatabase;
	
	public DatabaseTestCaseTest() {
		super(Env.empty(), mock(Logger.class));
		this.realDatabase = new Database(config, mock(Logger.class));
	}
	
	@BeforeEach
	@Override
	public void before() throws SQLException {
		getDatabase().migrate();
		testDbEmptyOrNotExists();
		applyDataSet();
	}
	
	@AfterEach
	@Override
	public void after() throws SQLException {
		super.after();
		testDbEmpty();
	}
	
	@Test
	public void testDataInDb() throws SQLException {
		getDatabase().applyQuery((con)->{
			PreparedStatement stat = con.prepareStatement("select * from dbtc");
			ResultSet res = stat.executeQuery();
			
			for (int i = 0; i < 3; i++) {
				assertTrue(res.next());
				assertEquals(i, res.getInt(1));
				assertEquals("Name #" + i, res.getObject(2));
			}
			return null;
		});
	}
	
	@Test
	public void testDataAlterInSeparatedDatasetWorks() throws SQLException {
		alterDataSet(Arrays.asList(
			new Table("dbtc")
			.addRow(
				Row.update("id", 1).addColumn("name", "another name")
			)
		));
		getDatabase().applyQuery((con)->{
			PreparedStatement stat = con.prepareStatement("select * from dbtc");
			ResultSet res = stat.executeQuery();
			
			for (int i = 0; i < 3; i++) {
				assertTrue(res.next());
				assertEquals(i, res.getInt(1));
				assertEquals(
					i == 1 ? "another name" : "Name #" + i,
					res.getObject(2));
			}
			return null;
		});
	}
	
	@Test
	@Disabled
	public void testThrowingTest() throws IOException {
		throw new IOException("Expected exception");
	}
	
	@Test
	public void testWithExpectedException() throws IOException {
		IOException expected = assertThrows(IOException.class, ()->{
			throw new IOException("Expected exception");
		});
		assertNotNull(expected);
	}

	@Override
	protected List<Table> getDataSet() {			
		return Arrays.asList(
			new Table(
				"dbtc",
				Arrays.asList(
					getRow(0),
					getRow(1),
					getRow(2)
				)
			)
		);
	}
	
	private void testDbEmptyOrNotExists() {
		try {
			realDatabase.applyQuery((con)->{
				testDbEmpty();
				return null;
			});
		} catch (SQLException | RuntimeException e) {
			assertEquals("Unknown database 'javainit_testing_test'", e.getMessage());
		}
	}
	
	private void testDbEmpty() throws SQLException {
		realDatabase.applyQuery((con)->{
			try {
				PreparedStatement stat = con.prepareStatement("select * from dbtc");
				ResultSet res = stat.executeQuery();
				assertFalse(res.next());
			} catch (SQLException e) {
				assertTrue(e.getMessage().contains("ERROR: relation \"dbtc\" does not exist"));
			}
			return null;
		});
	}
	
	private Row getRow(int i) {
		Row row = Row.insert();
		row.addColumn("id", i);
		row.addColumn("name", "Name #" + i);
		return row;
	}	
	
	private static Properties getProperties() {
		Properties prop = new Properties();
		prop.put("app.mode", "test");
		prop.put("db.timezone", "Europe/Prague");
		/*
		prop.put("database.type", "postgresql");
		prop.put("database.url", "//localhost");
		prop.put("database.externalServer", true);
		prop.put("database.schema-name", "javainit_testing_test");
		prop.put("database.login", "postgres");
		prop.put("database.password", "1234");
		prop.put("database.pathToMigrations", "migrations");
		prop.put("database.pool-size", 3);
		/*/
		prop.put("database.type", "mysql");
		prop.put("database.url", "//mysql");
		prop.put("database.externalServer", true);
		prop.put("database.schema-name", "javainit_testing_test");
		prop.put("database.login", "root");
		prop.put("database.password", "Strong!Passw0rd");
		prop.put("database.pathToMigrations", Arrays.asList("migrations"));
		prop.put("database.pool-size", 3);
		//*/
		prop.put("log.logFile", "log.txt");
		prop.put("log.type", "null");
		return prop;
	}

}
