package ji.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.SqlServerQueryBuilder;

public class SqlServer implements DatabaseInstance {
	
	// private final Logger logger;
	
	private final String connectionString;
	
	private final Properties property;
	
	private final String name;

	public SqlServer(String connectionString, Properties property, String name, final Logger logger) {
		// this.logger = logger;
		this.connectionString = connectionString;
		this.property = property;
		this.name = name;
	}

	@Override
	public void createDb() throws SQLException {
		DriverManager
			.getConnection(connectionString, property)
			.createStatement()
			.executeUpdate(String.format(
				 "IF NOT EXISTS("
				 + "SELECT * FROM sys.databases WHERE name = '%s'"
				 + ")"
				 + " BEGIN" + 
				 "	CREATE DATABASE %s" + 
				 "  END",
				 name, name
			));
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new SqlServerQueryBuilder();
	}

}
