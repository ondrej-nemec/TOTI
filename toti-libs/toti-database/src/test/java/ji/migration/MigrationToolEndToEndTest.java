package ji.migration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.instances.MySqlQueryBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class MigrationToolEndToEndTest {
	
	////////////////////
	// v db jsou migrace - vrati vsechna id
	// v db neni migrace, ale je tabulka - vrati prazdne
	// neni db - vrati prazdne + vytvory tabulku
	////////////
	// zacatek transakce
	// provedeni migrace
	// zapis do tabulky migraci
	// commit migrace
	
	// migrace provedena, zaznam v tabulce
	////////////////
	// zacatek transakce
	// vrzeni v migraci
	// rollback
	
	// test nezapsana migrace, nejsou zmeny
	/////////////////////////

	
	private final String dbName = "javainit_migration_test";
	
	@Test
	public void testMigrateOnSecondTime() throws Exception {
		Connection c = createConnection();
		c.setAutoCommit(false);
		
		QueryBuilder queryBuilder = new QueryBuilder(new MySqlQueryBuilder(), c);
		try {
			MigrationTool tableCreate = new MigrationTool(Arrays.asList("empty"), queryBuilder, Mockito.mock(Logger.class));
			tableCreate.migrate();
		} catch (Exception ignored) {
			// this block for creating table
		}		
		queryBuilder.insert("migrations")
			.addValue("id", "A")
			.addValue("Description", "desc")
			.addValue("DateTime", "2020-11-21 9:00:00")
			.addValue("Module", "test/migA")
			.execute();
		
		MigrationTool tool = new MigrationTool(
			Arrays.asList("test/migA", "test/migB"), 
			queryBuilder,
			Mockito.mock(Logger.class)
		);
		tool.migrate();
		
		c.rollback();
		c.close();
	}
	
	@Test
	@Parameters(method = "dataMigrateMakeMigrations")
	public void testMigrateMakeMigrations(String folder) throws Exception {
		Connection c = createConnection();
		c.setAutoCommit(false);
		
		QueryBuilder queryBuilder = new QueryBuilder(new MySqlQueryBuilder(), c);
		MigrationTool tool = new MigrationTool(Arrays.asList(folder), queryBuilder, Mockito.mock(Logger.class));
		
		testStates(c, false);
		tool.migrate();
		testStates(c, true);
		tool.revert();
		testStates(c, false);
		
		c.rollback();
		c.close();
	}
	
	private void testStates(Connection c, boolean exists) throws SQLException {
		String[] tables = new String[] {"First_table", "Second_table", "migrations"};
		for (String table : tables) {
			try {
				c.prepareStatement("select * from " + table).execute();
			} catch (SQLException e) {
				if (exists) {
					throw e;
				}
			}
		}
	}

	public Object[] dataMigrateMakeMigrations() {
		return new Object[] {
			new Object[] {
				"migration/endToEnd"
			},
			new Object[] {
				"test/migration"
			}
		};
	}
	
	@Test
	public void testMigrateModulesMakeMigrations() throws Exception {
		Connection c = createConnection();
		c.setAutoCommit(false);
		
		QueryBuilder queryBuilder = new QueryBuilder(new MySqlQueryBuilder(), c);
		MigrationTool tool = new MigrationTool(
			Arrays.asList("migration/endToEndModule1", "migration/endToEndModule2"), 
			queryBuilder, 
			Mockito.mock(Logger.class)
		);
		
		testStates(c, false);
		tool.migrate();
		c.commit();
		testStates(c, true);
		tool.revert();
		testStates(c, false);
		
		c.rollback();
		c.close();
	}
	
	@Test(expected = IOException.class)
	public void testMigrateThrowsIfNotExistingFolderGiven() throws Exception {
		try (Connection c = createConnection()) {
			QueryBuilder queryBuilder = new QueryBuilder(new MySqlQueryBuilder(), c);
			MigrationTool tool = new MigrationTool(Arrays.asList("not-existing-folder"), queryBuilder, Mockito.mock(Logger.class));
			tool.migrate();
		}
	}
	
	private Connection createConnection() throws SQLException {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "");
		prop.setProperty("serverTimezone", "Europe/Prague");		

		DriverManager
			.getConnection("jdbc:mysql://localhost:3306/", prop)
			.prepareStatement("DROP SCHEMA IF EXISTS " + dbName)
			.execute();
		
		DriverManager
			.getConnection("jdbc:mysql://localhost:3306/", prop)
			.createStatement()
			.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, prop);
	}
	
}
