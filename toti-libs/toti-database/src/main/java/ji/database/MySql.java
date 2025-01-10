package ji.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.MySqlQueryBuilder;

public class MySql implements DatabaseInstance {
	
	private final Logger logger;
	
	private final String connectionString;
	
	private final Properties property;
	
	private final String name;
	
	public MySql(String connectionString, Properties property, String name, final Logger logger) {
		this.logger = logger;
		this.connectionString = connectionString;
		this.property = property;
		this.name = name;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			this.logger.warn("MySQL driver could not be registered", e);
		}
	}

	@Override
	public void createDb() throws SQLException {
		DriverManager
			.getConnection(connectionString, property)
			.createStatement()
			.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new MySqlQueryBuilder();
	}

}
