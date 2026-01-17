package toti.lib.database.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.instances.PostgreSqlQueryBuilder;

public class PosgreSql implements DatabaseInstance {
	
	private final String baseConnectionString;
	
	private final Properties property;
	
	private final String name;
	
	private final Logger logger;
	
	public PosgreSql(String baseConnectionString, Properties property, String name, Logger logger) {
		if (!baseConnectionString.endsWith("/")) {
			baseConnectionString += "/";
		}
		this.baseConnectionString = baseConnectionString;
		this.property = property;
		this.logger = logger;
		this.name = name;
		try {
			Class.forName("org.postgresql.Driver");
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
			throw new NotImplementedYet(); // TODO start postgres server if not external
		}
	}

	@Override
	public void stopServer() {
		if (runOnExternal) {
			logger.info("Signal Stop DB server not sended because server is not under app manage");
		} else {
			throw new NotImplementedYet(); // TODO stop postgres server if not external
		}
	}
*/
	@Override
	public void createDb() throws SQLException {
		// always need some database specified. if no db, same as user is used. can cause problems
		try (Connection con = DriverManager.getConnection(baseConnectionString + "postgres", property)) {
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

	@Override
	public String getConnectionString() {
		return this.baseConnectionString + name;
	}

}
