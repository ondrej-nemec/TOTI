package ji.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.PostgreSqlQueryBuilder;

public class PosgreSql implements DatabaseInstance {
	
	private final String connectionString;
	
	private final Properties property;
	
	private final String name;
	
	private final Logger logger;
	
	public PosgreSql(String connectionString, Properties property, String name, Logger logger) {
		this.connectionString = connectionString;
		this.property = property;
		this.logger = logger;
		this.name = name;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			this.logger.warn("MySQL driver could not be registered", e);
		}
	}

	@Override
	public void createDb() throws SQLException {
		try (Connection con = DriverManager.getConnection(connectionString, property)) {
			PreparedStatement stmt = con.prepareStatement("SELECT FROM pg_database WHERE datname = ?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				con.createStatement().executeUpdate(String.format("CREATE DATABASE %s", name));
			}
		}
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new PostgreSqlQueryBuilder();
	}

}
