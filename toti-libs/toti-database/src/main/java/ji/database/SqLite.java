package ji.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.SqLiteQueryBuilder;

public class SqLite implements DatabaseInstance {
	
//	private final Logger logger;
	
	private final String connectionString;
	
	private final Properties property;
	
	private final String name;

	//private final boolean runOnExternal;
	
	public SqLite(boolean runOnExternal, String connectionString, Properties property, String name, final Logger logger) {
	//	this.logger = logger;
	//	this.runOnExternal = runOnExternal;
		this.connectionString = connectionString;
		this.property = property;
		this.name = name;
		/*try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			this.logger.warn("MySQL driver could not be registered", e);
		}*/
	}

	@Override
	public void startServer() {}

	@Override
	public void stopServer() {}

	@Override
	public void createDb() throws SQLException {
		DriverManager
    		.getConnection(connectionString, property)
    		.createStatement()
    		.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new SqLiteQueryBuilder();
	}

}
