package ji.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connections {
	
	public static final String QUERY_BUILDER_TABLE = "query_builder";
	private static final String PASSWORD = "Strong!Passw0rd";
	
	private final String tableName;
	
	public static Connections QUERY_BUILDER() {
		return new Connections(QUERY_BUILDER_TABLE);
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
		File path = new File("../../volumes/sqlite/" + tableName + ".db");
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
	
	public Connection postgres() throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("allowMultiQueries", "true");
		// return DriverManager.getConnection("jdbc:postgresql://postgres:5432/" + tableName, props);
		return DriverManager.getConnection("jdbc:postgresql://localhost:19060/" + tableName, props);
	}
	
	public Connection sqlserver() throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "sa");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		//return DriverManager.getConnection("jdbc:sqlserver://sqlserver:1434;databaseName=" + tableName, props);
		return DriverManager.getConnection("jdbc:sqlserver://localhost:19050;databaseName=" + tableName, props);
	}
	
	public Connection mysql() throws SQLException {
		return mysqlBase("/" + tableName);
	}
	
	public static Connection mysqlBase() throws SQLException {
		return mysqlBase("");
	}
	
	private static Connection mysqlBase(String tableName) throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", PASSWORD);
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
	//	return DriverManager.getConnection("jdbc:mysql://mysql:3306/" + tableName, props);
		return DriverManager.getConnection("jdbc:mysql://localhost:19070" + tableName, props);
	}
	
}
