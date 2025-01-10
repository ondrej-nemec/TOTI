package ji.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Terminal;
import ji.querybuilder.DbInstance;
import ji.querybuilder.instances.DerbyQueryBuilder;

public class Derby implements DatabaseInstance {

	private final Terminal terminal;
	
	private final Logger logger;
	
	private final String pathToServer;
	
	private final String connectionString;
	
	private final Properties property;
	
	public Derby(final String pathToServer, String connectionString, Properties property, final Logger logger) {
		this.terminal = new Terminal(logger);
		this.logger = logger;
		this.connectionString = connectionString;
		this.property = property;
		this.pathToServer = pathToServer;
	}

	public void startServer() {
		//	System.getProperties().setProperty("derby.system.home", config.pathOrUrlToLocation);
		terminal.runFile(pathToServer + "/startNetworkServer");
		logger.info("Derby has been started");
	}

	public void stopServer() {
		terminal.runFile(pathToServer + "/stopNetworkServer");
		logger.info("Derby has been shutdowned");
	}

	@Override
	public void createDb() throws SQLException {
		try (Connection con = DriverManager.getConnection(connectionString, property)) {
			/* create and close connection - create db schema */
		}
	}

	@Override
	public DbInstance getBuilderInstance() {
		return new DerbyQueryBuilder();
	}

}
