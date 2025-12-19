package ji.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connections {
	
	public static final String QUERY_BUILDER_DATABASE = "query_builder";
	private static final String PASSWORD = "Strong!Passw0rd";
	
	private final String tableName;
	
	public static Connections QUERY_BUILDER() {
		return new Connections(QUERY_BUILDER_DATABASE);
	}
	
	private Connections(String tableName) {
		this.tableName = tableName;
	}

	public Connection sqlite() throws SQLException {
		Properties props = new Properties();
		//props.setProperty("user", "root");
		//props.setProperty("password", "SomeP@ssw0rd");
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		File path = getSqliteFile();
		try {
			return DriverManager.getConnection(
				"jdbc:sqlite:" + path.getCanonicalPath(),
				props
			);
		} catch (IOException e) {
			throw new SQLException(e);
		}
		//return DriverManager.getConnection("jdbc:sqlite:volumes/sqlite/query_builder.db", props);
	}

	public File getSqliteFile() {
		return new File("../../volumes/sqlite/" + tableName + ".db");
	}
	
	public Connection postgres() throws SQLException {
		return _postgres(tableName);
	}
	
	public Connection postgresBase() throws SQLException {
		return _postgres(null);
	}
	
	public Connection _postgres(String table) throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("allowMultiQueries", "true");
		return DriverManager.getConnection(
			"jdbc:postgresql://postgres:5432/" + (table == null ? "" : table),
			props
		);
	}
	
	public Connection sqlserver() throws SQLException {
		return _sqlserver(tableName);
	}
	
	public Connection sqlserverBase() throws SQLException {
		return _sqlserver(null);
	}
	
	public Connection _sqlserver(String table) throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "sa");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		props.setProperty("Encrypt", "false");
		return DriverManager.getConnection(
			"jdbc:sqlserver://sqlserver:1433;" + (table == null ? "" : "databaseName=" + table),
			props
		);
	}
	
	public Connection mysql() throws SQLException {
		return _mysql("/" + tableName);
	}
	
	public Connection mysqlBase() throws SQLException {
		return _mysql("");
	}
	
	private static Connection _mysql(String tableName) throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		return DriverManager.getConnection("jdbc:mysql://mysql:3306" + tableName, props);
	}
	
}
