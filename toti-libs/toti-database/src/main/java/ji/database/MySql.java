package ji.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.MySqlQueryBuilder;

public class MySql implements DatabaseInstance {
	
	private final Logger logger;
	
	private final String baseConnectionString;
	
	private final Properties property;
	
	private final String name;
	
	public MySql(String baseConnectionString, Properties property, String name, final Logger logger) {
		this.logger = logger;
		this.baseConnectionString = baseConnectionString;
		this.property = property;
		this.name = name;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			this.logger.warn("MySQL driver could not be registered", e);
		}
	}
/*
	@Override
	public void startServer() {
		if (runOnExternal) {
			logger.info("Signal Start DB server not sended because server is not under app manage");
		} else {
			throw new NotImplementedYet(); // TODO start mysql server if not external
		}
	}

	@Override
	public void stopServer() {
		if (runOnExternal) {
			logger.info("Signal Stop DB server not sended because server is not under app manage");
		} else {
			throw new NotImplementedYet(); // TODO stop mysql server if not external
		}
	}
*/
	@Override
	public void createDb() throws SQLException {
		DriverManager
    		.getConnection(baseConnectionString, property)
    		.createStatement()
    		.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new MySqlQueryBuilder();
	}

	@Override
	public String getConnectionString() {
		return this.baseConnectionString + "/" + name;
	}

}
