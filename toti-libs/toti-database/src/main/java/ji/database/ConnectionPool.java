package ji.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.Logger;

import ji.database.support.SqlQueryProfiler;
import ji.database.wrappers.ConnectionWrapper;

public class ConnectionPool {
	
	private final static int WAIT_TIME = 30; // miliseconds
	private final static int VALID_TIMEOUT = 3; // seconds
	
	private final boolean isTemp;
	private final String connectionString;
	private final Properties prop;
	private final int maxSize;
	
	private final Logger logger;
	
	private final ConcurrentLinkedDeque<Connection> available;
	private final Map<Integer, Connection> borrowed;
	
	private final SqlQueryProfiler profiler;
	
	public ConnectionPool(String connectionString, Properties prop, int maxSize, Logger logger, boolean temp, SqlQueryProfiler profiler) {
		this.maxSize = maxSize;
		this.isTemp = temp;
		this.prop = prop;
		this.connectionString = connectionString;
		this.logger = logger;
		this.profiler = profiler;
		//this.available = new LinkedList<>();
		//this.borrowed = new HashMap<>();
		this.available = new ConcurrentLinkedDeque<>();
		this.borrowed = Collections.synchronizedMap(new HashMap<Integer, Connection>());
	}
	
	/**
	 * BLOCKING
	 * Create connection or get from pool
	 * @return connection
	 * @throws SQLException
	 */
	public synchronized Connection getConnection() throws SQLException {
		// borrowed full - wait
		while (borrowed.size() >= maxSize) {
		//	System.err.println(String.format("No connection: A: %s, B: %s", available.size(), borrowed.size()));
			logger.debug("Connection pool is busy, waiting. " + getState());
			try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
		}
		logger.debug("Try get connnection." + getState());
		Connection c = getAvailableConnection();
	//	System.err.println(String.format("Get connection: A: %s, B: %s", available.size(), borrowed.size()));
		c.setAutoCommit(!isTemp);
		borrowed.put(c.hashCode(), c);
		logger.debug("Connection borrowed: " + c.hashCode() + ". " + getState());
		return c;
	}

	private Connection getAvailableConnection() throws SQLException {
		try {
			Connection poll = available.poll();
			if (poll == null) {
                Connection c = createConnection();
                logger.debug("Connection created: " + c.hashCode() + " ." + getState());
                return c;
            }
			if (!poll.isValid(VALID_TIMEOUT)) {
                logger.debug("Closing invalid connection: " + poll.hashCode() + " ." + getState());
                poll.close();
                return getAvailableConnection();
             }
             return poll;
		} catch (Exception e) {
            logger.error("Get connection: Available: " + available.size() + ", Borrowed:" + borrowed.size(), e);
            throw new SQLException(e);
       }
		/*
		// no available - create
		if (available.size() == 0) {
			Connection c = createConnection();
			logger.debug("Connection created: " + c.hashCode());
			return c;
		}
		Connection c = available.removeFirst();
		if (!c.isValid(VALID_TIMEOUT)) {
            logger.debug("Closing invalid connection: " + c.hashCode() + " ." + getState());
            c.close();
			return getAvailableConnection();
		}
		return c;
		*/
	}
	
	private Connection createConnection() throws SQLException {
		Connection c = DriverManager.getConnection(connectionString, prop);
		if (profiler == null) {
			return c;
		}
		return new ConnectionWrapper(c, profiler);
	}
	
	public void returnAllConnections() throws SQLException {		
		for (Integer key : borrowed.keySet()) {
			returnConnection(borrowed.get(key));
		}
	}
	
	public void returnConnection(Connection connection) throws SQLException {
		if (isTemp) {
			connection.rollback();
		}
		available.add(
			borrowed.remove(connection.hashCode())
		);
		logger.debug("Connection returned: " + connection.hashCode() + ". " + getState());
	//	System.err.println(String.format("Return connection: A: %s, B: %s", available.size(), borrowed.size()));
	}

	public void close() throws SQLException {
		returnAllConnections();
		for (Connection c : available) {
			c.close();
		}
		logger.info("Connection pool was closed");
	}
	
	/***************/
	
	private String getState() {
		return String.format("Borrowed/available %s/%s", borrowed.size(), available.size());
	}

}
